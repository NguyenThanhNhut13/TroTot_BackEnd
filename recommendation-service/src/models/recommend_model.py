import pickle
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import MinMaxScaler, OneHotEncoder
import pandas as pd
import os

def extract_features(rooms_data):
    df = pd.DataFrame(rooms_data)
    
    required_fields = ['roomId', 'price', 'area', 'description', 'amenities', 'targetAudiences', 'surroundingAreas', 'selfManaged', 'forGender', 'roomType', 'province', 'district']
    missing_fields = [field for field in required_fields if field not in df.columns]
    if missing_fields:
        raise ValueError(f"Thiếu các trường bắt buộc: {missing_fields}")
    
    numerical_features = df[['price', 'area']].values
    scaler = MinMaxScaler()
    numerical_scaled = scaler.fit_transform(numerical_features)
    
    df['selfManaged'] = df['selfManaged'].astype(int)
    categorical_features = ['forGender', 'roomType', 'province', 'district']
    onehot = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
    categorical_encoded = onehot.fit_transform(df[categorical_features])
    
    tfidf = TfidfVectorizer(max_features=100)
    tfidf_matrix = tfidf.fit_transform(df['description'].fillna('')).toarray()
    
    def multi_hot_encode(column, df):
        all_items = set()
        for items in df[column]:
            if items and items != 'unknown':
                all_items.update(items.split(', '))
        if not all_items:
            return np.zeros((len(df), 1)), ['unknown']
        multi_hot = np.array([[1 if item in items.split(', ') else 0 for item in all_items] for items in df[column]])
        return multi_hot, all_items
    
    amenities_matrix, all_amenities = multi_hot_encode('amenities', df)
    target_audiences_matrix, all_target_audiences = multi_hot_encode('targetAudiences', df)
    surrounding_areas_matrix, all_surrounding_areas = multi_hot_encode('surroundingAreas', df)
    
    feature_matrix = np.hstack((
        numerical_scaled,
        df[['selfManaged']].values,
        categorical_encoded,
        tfidf_matrix,
        amenities_matrix,
        target_audiences_matrix,
        surrounding_areas_matrix
    ))
    
    return (feature_matrix, tfidf, scaler, onehot, 
            all_amenities, all_target_audiences, all_surrounding_areas)

class RecommendModel:
    def __init__(self):
        self.rooms_df = None
        self.similarity_matrix = None
        self.tfidf = None
        self.scaler = None
        self.onehot = None
        self.all_amenities = None
        self.all_target_audiences = None
        self.all_surrounding_areas = None

    def train(self, rooms_data):
        if not rooms_data:
            raise ValueError("Dữ liệu phòng trọ trống")
        
        self.rooms_df = pd.DataFrame(rooms_data)
        
        try:
            (feature_matrix, self.tfidf, self.scaler, self.onehot, 
             self.all_amenities, self.all_target_audiences, self.all_surrounding_areas) = extract_features(rooms_data)
        except Exception as e:
            raise ValueError(f"Lỗi khi trích xuất đặc trưng: {str(e)}")
        
        try:
            self.similarity_matrix = cosine_similarity(feature_matrix)
        except Exception as e:
            raise ValueError(f"Lỗi khi tính ma trận tương đồng: {str(e)}")
        
        os.makedirs('models', exist_ok=True)
        
        try:
            with open('models/tfidf.pkl', 'wb') as f:
                pickle.dump(self.tfidf, f)
            with open('models/scaler.pkl', 'wb') as f:
                pickle.dump(self.scaler, f)
            with open('models/onehot.pkl', 'wb') as f:
                pickle.dump(self.onehot, f)
            with open('models/all_amenities.pkl', 'wb') as f:
                pickle.dump(self.all_amenities, f)
            with open('models/all_target_audiences.pkl', 'wb') as f:
                pickle.dump(self.all_target_audiences, f)
            with open('models/all_surrounding_areas.pkl', 'wb') as f:
                pickle.dump(self.all_surrounding_areas, f)
            with open('models/similarity_matrix.pkl', 'wb') as f:
                pickle.dump(self.similarity_matrix, f)
            with open('models/rooms_df.pkl', 'wb') as f:
                pickle.dump(self.rooms_df, f)
        except Exception as e:
            raise ValueError(f"Lỗi khi lưu mô hình: {str(e)}")

    def load_models(self):
        try:
            with open('models/tfidf.pkl', 'rb') as f:
                self.tfidf = pickle.load(f)
            with open('models/scaler.pkl', 'rb') as f:
                self.scaler = pickle.load(f)
            with open('models/onehot.pkl', 'rb') as f:
                self.onehot = pickle.load(f)
            with open('models/all_amenities.pkl', 'rb') as f:
                self.all_amenities = pickle.load(f)
            with open('models/all_target_audiences.pkl', 'rb') as f:
                self.all_target_audiences = pickle.load(f)
            with open('models/all_surrounding_areas.pkl', 'rb') as f:
                self.all_surrounding_areas = pickle.load(f)
            with open('models/similarity_matrix.pkl', 'rb') as f:
                self.similarity_matrix = pickle.load(f)
            with open('models/rooms_df.pkl', 'rb') as f:
                self.rooms_df = pickle.load(f)
        except FileNotFoundError:
            raise ValueError("Mô hình chưa được huấn luyện. Vui lòng chạy endpoint /train trước.")
        except Exception as e:
            raise ValueError(f"Lỗi khi tải mô hình: {str(e)}")

    def get_similar_rooms(self, room_id, limit=5):
        if self.rooms_df is None or 'roomId' not in self.rooms_df.columns:
            raise ValueError("Không tìm thấy dữ liệu phòng hoặc roomId")
        
        room_idx = self.rooms_df.index[self.rooms_df['roomId'] == room_id].tolist()
        if not room_idx:
            raise ValueError(f"Không tìm thấy phòng với id {room_id}")
        
        room_idx = room_idx[0]
        sim_scores = self.similarity_matrix[room_idx]
        top_indices = np.argsort(sim_scores)[::-1][1:limit+1]
        return self.rooms_df['roomId'].iloc[top_indices].tolist()