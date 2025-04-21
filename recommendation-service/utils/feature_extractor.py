import numpy as np
from sklearn.preprocessing import MinMaxScaler
from sklearn.feature_extraction.text import TfidfVectorizer

def extract_features(rooms_data):
    """
    Trích xuất đặc trưng từ dữ liệu phòng trọ.
    Bao gồm: numerical features, TF-IDF cho description, và one-hot cho amenities.
    
    :param rooms_data: Danh sách các dictionary chứa thông tin phòng trọ.
    :return: Ma trận đặc trưng, đối tượng TF-IDF, đối tượng scaler, và danh sách amenities.
    """
    # Numerical features: price, area
    numerical_features = np.array([[room['price'], room['area']] for room in rooms_data])
    scaler = MinMaxScaler()
    numerical_scaled = scaler.fit_transform(numerical_features)
    
    # Text features: description (sử dụng TF-IDF)
    descriptions = [room['description'] for room in rooms_data]
    tfidf = TfidfVectorizer(stop_words='english', max_features=100)  # Giới hạn 100 features
    tfidf_matrix = tfidf.fit_transform(descriptions).toarray()
    
    # Amenities: mã hóa one-hot
    all_amenities = set()
    for room in rooms_data:
        all_amenities.update(room['amenities'].split(', '))
    amenities_matrix = np.array([[1 if amenity in room['amenities'].split(', ') else 0 
                                  for amenity in all_amenities] for room in rooms_data])
    
    # Kết hợp tất cả features
    feature_matrix = np.hstack((numerical_scaled, tfidf_matrix, amenities_matrix))
    return feature_matrix, tfidf, scaler, all_amenities