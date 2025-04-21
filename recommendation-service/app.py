from fastapi import FastAPI
import requests
import pickle
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.decomposition import TruncatedSVD
import pandas as pd
import schedule
import time
import threading
import os
from utils.feature_extractor import extract_features

app = FastAPI()

# Config từ Spring Cloud Config
CONFIG_SERVER_URL = "http://localhost:8888"
PROFILE = os.getenv('PROFILE', 'default')
API_GATEWAY_URL = None

def get_config():
    response = requests.get(f"{CONFIG_SERVER_URL}/recommend-service/{PROFILE}")
    config = response.json()
    return config['propertySources'][0]['source']

config = get_config()
API_GATEWAY_URL = config['api.gateway.url']

# Lấy dữ liệu từ các dịch vụ khác
def get_rooms_data():
    response = requests.get(f"{API_GATEWAY_URL}/room-service/rooms")
    return response.json()

def get_interactions_data():
    response = requests.get(f"{API_GATEWAY_URL}/review-service/reviews")
    return response.json()

# Class mô hình gợi ý
class RecommendModel:
    def __init__(self):
        self.similarity_matrix = None
        self.svd_model = None
        self.rooms_df = None
        self.tfidf = None
        self.scaler = None
        self.all_amenities = None
        self.user_ids = None
        self.item_ids = None
        self.user_item_matrix = None

    def train(self, rooms_data, interactions_data):
        self.rooms_df = pd.DataFrame(rooms_data)
        
        # Trích xuất features với TF-IDF
        feature_matrix, self.tfidf, self.scaler, self.all_amenities = extract_features(rooms_data)
        self.similarity_matrix = cosine_similarity(feature_matrix)
        
        # Chuẩn bị dữ liệu cho SVD
        interactions_df = pd.DataFrame(interactions_data)
        self.user_ids = interactions_df['userId'].unique()
        self.item_ids = self.rooms_df['roomId'].unique()
        
        # Tạo ma trận user-item
        user_item_data = interactions_df.pivot(index='userId', columns='roomId', values='rating').fillna(0)
        self.user_item_matrix = user_item_data.values
        
        # Huấn luyện mô hình SVD
        self.svd_model = TruncatedSVD(n_components=50)
        self.svd_model.fit(self.user_item_matrix)
        
        # Lưu mô hình và các đối tượng liên quan
        with open('models/similarity_matrix.pkl', 'wb') as f:
            pickle.dump(self.similarity_matrix, f)
        with open('models/svd_model.pkl', 'wb') as f:
            pickle.dump(self.svd_model, f)
        with open('models/tfidf.pkl', 'wb') as f:
            pickle.dump(self.tfidf, f)
        with open('models/scaler.pkl', 'wb') as f:
            pickle.dump(self.scaler, f)
        with open('models/all_amenities.pkl', 'wb') as f:
            pickle.dump(self.all_amenities, f)
        with open('models/user_ids.pkl', 'wb') as f:
            pickle.dump(self.user_ids, f)
        with open('models/item_ids.pkl', 'wb') as f:
            pickle.dump(self.item_ids, f)

    def load_models(self):
        with open('models/similarity_matrix.pkl', 'rb') as f:
            self.similarity_matrix = pickle.load(f)
        with open('models/svd_model.pkl', 'rb') as f:
            self.svd_model = pickle.load(f)
        with open('models/tfidf.pkl', 'rb') as f:
            self.tfidf = pickle_load(f)
        with open('models/scaler.pkl', 'rb') as f:
            self.scaler = pickle.load(f)
        with open('models/all_amenities.pkl', 'rb') as f:
            self.all_amenities = pickle.load(f)
        with open('models/user_ids.pkl', 'rb') as f:
            self.user_ids = pickle.load(f)
        with open('models/item_ids.pkl', 'rb') as f:
            self.item_ids = pickle.load(f)

    def get_similar_rooms(self, room_id, limit=5):
        room_idx = self.rooms_df.index[self.rooms_df['roomId'] == room_id].tolist()[0]
        sim_scores = self.similarity_matrix[room_idx]
        top_indices = np.argsort(sim_scores)[::-1][1:limit+1]  # Bỏ qua chính nó
        return self.rooms_df['roomId'].iloc[top_indices].tolist()

    def get_user_recommendations(self, user_id, limit=5):
        if user_id not in self.user_ids:
            return self.get_popular_rooms(limit)
        
        user_index = np.where(self.user_ids == user_id)[0][0]
        user_vector = self.svd_model.transform(self.user_item_matrix[user_index].reshape(1, -1))
        scores = self.svd_model.inverse_transform(user_vector).flatten()
        top_items = self.item_ids[np.argsort(-scores)][:limit]
        return top_items.tolist()

    def get_popular_rooms(self, limit=5):
        # Giả sử rooms_df có cột 'popularity' (tính từ lượt xem hoặc đánh giá)
        return self.rooms_df.sort_values('popularity', ascending=False)['roomId'].head(limit).tolist()

model = RecommendModel()

# Huấn luyện mô hình lúc 12h đêm
def train_model():
    rooms_data = get_rooms_data()
    interactions_data = get_interactions_data()
    model.train(rooms_data, interactions_data)

schedule.every().day.at("00:00").do(train_model)

def run_scheduler():
    while True:
        schedule.run_pending()
        time.sleep(1)

threading.Thread(target=run_scheduler, daemon=True).start()

# Đăng ký với Eureka
EUREKA_URL = "http://localhost:8761/eureka"

def register_with_eureka():
    instance = {
        "instanceId": "recommend-service:5000",
        "hostName": "localhost",
        "app": "recommend-service",
        "ipAddr": "127.0.0.1",
        "port": {"$": 5000, "@enabled": "true"},
        "healthCheckUrl": "http://localhost:5000/health",
        "statusPageUrl": "http://localhost:5000/info",
        "homePageUrl": "http://localhost:5000"
    }
    requests.post(f"{EUREKA_URL}/apps/recommend-service", json=instance)

def send_heartbeat():
    while True:
        requests.put(f"{EUREKA_URL}/apps/recommend-service/recommend-service:5000")
        time.sleep(30)

register_with_eureka()
threading.Thread(target=send_heartbeat, daemon=True).start()

# API Endpoints
@app.post("/api/v1/recommend/train")
def train():
    train_model()
    return {"message": "Model trained successfully"}

@app.get("/api/v1/recommend/similar/{room_id}")
def get_similar_rooms(room_id: int, limit: int = 5):
    model.load_models()
    similar_rooms = model.get_similar_rooms(room_id, limit)
    return {"similar_rooms": similar_rooms}

@app.get("/api/v1/recommend/user/{user_id}")
def get_user_recommendations(user_id: int, limit: int = 5):
    model.load_models()
    recommendations = model.get_user_recommendations(user_id, limit)
    return {"recommendations": recommendations}

@app.get("/health")
def health_check():
    return {"status": "UP"}