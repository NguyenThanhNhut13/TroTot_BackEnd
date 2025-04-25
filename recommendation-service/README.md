# TroTot - Dịch vụ Gợi ý Phòng Trọ

## Giới thiệu

Dịch vụ Gợi ý (Recommendation Service) là một microservice trong hệ thống TroTot, có nhiệm vụ cung cấp các gợi ý phòng trọ phù hợp cho người dùng dựa trên hành vi và sở thích của họ, cũng như các thuộc tính của phòng trọ.

Dịch vụ sử dụng hai phương pháp gợi ý chính:
- **Content-based Filtering**: Gợi ý phòng dựa trên đặc điểm tương tự của các phòng trọ
- **Collaborative Filtering**: Gợi ý dựa trên hành vi người dùng (xem và lưu phòng)

## Yêu cầu hệ thống

- Python 3.8 trở lên
- Redis server
- Config Server (Spring Cloud Config)
- Eureka Server (Service Discovery)
- API Gateway

## Cài đặt

### 1. Cài đặt các thư viện phụ thuộc

```bash
pip install -r requirements.txt
```

### 2. Cấu hình Redis

Đảm bảo Redis đã được cài đặt và chạy. Thông tin kết nối sẽ được lấy từ Config Server.

### 3. Cấu hình Eureka và Config Server

Đảm bảo các dịch vụ sau đã chạy:
- Config Server (mặc định tại http://localhost:8888)
- Eureka Server (mặc định tại http://localhost:8761)

## Chạy dịch vụ

### Chế độ phát triển

```bash
uvicorn src.main:app --reload --host 0.0.0.0 --port 5000
```

### Chế độ sản phẩm

```bash
uvicorn src.main:app --host 0.0.0.0 --port 5000
```

### Với Docker

```bash
docker build -t trotot-recommendation-service .
docker run -p 5000:5000 trotot-recommendation-service
```

## Cấu trúc dự án

```
recommendation-service/
├── src/
│   ├── clients/             # Kết nối với các dịch vụ khác
│   ├── models/              # Mô hình gợi ý
│   │   ├── recommend_model.py   # Mô hình content-based
│   │   └── user_behavior_model.py  # Mô hình collaborative
│   ├── routes/              # API endpoints
│   │   └── recommend.py     # Định nghĩa các API
│   └── main.py              # Điểm khởi chạy ứng dụng
├── Dockerfile               # Cấu hình Docker
├── requirements.txt         # Thư viện phụ thuộc
└── README.md                # Tài liệu hướng dẫn
```

## API Endpoints

### 1. Huấn luyện mô hình

```
POST /api/v1/recommend/train
```

**Mô tả**: Huấn luyện mô hình gợi ý với dữ liệu phòng trọ mới nhất

**Phản hồi**:
```json
{
  "success": true,
  "message": "Huấn luyện mô hình thành công",
  "data": null
}
```

### 2. Theo dõi hành vi xem phòng

```
POST /api/v1/recommend/track/view
```

**Body**:
```json
{
  "user_id": 123,
  "room_id": 456
}
```

**Mô tả**: Ghi lại hành vi người dùng xem phòng để cải thiện đề xuất

**Phản hồi**:
```json
{
  "success": true,
  "message": "Đã theo dõi hành vi xem phòng",
  "data": null
}
```

### 3. Theo dõi hành vi lưu phòng

```
POST /api/v1/recommend/track/save
```

**Body**:
```json
{
  "user_id": 123,
  "room_id": 456
}
```

**Mô tả**: Ghi lại hành vi người dùng lưu phòng để cải thiện đề xuất

**Phản hồi**:
```json
{
  "success": true,
  "message": "Đã ghi nhận hành vi lưu phòng",
  "data": null
}
```

### 4. Gợi ý phòng cho người dùng

```
GET /api/v1/recommend/user/{user_id}?limit=5
```

**Tham số**:
- `user_id`: ID của người dùng cần gợi ý phòng
- `limit`: Số lượng phòng muốn gợi ý (mặc định: 5)

**Mô tả**: Trả về danh sách phòng trọ được gợi ý cho người dùng cụ thể

**Phản hồi thành công**:
```json
{
  "success": true,
  "message": "Lấy gợi ý thành công",
  "data": [
    {
      "id": 123,
      "title": "Phòng trọ quận 1",
      // Các thông tin khác của phòng...
    },
    // ...
  ]
}
```

### 5. Tìm phòng tương tự

```
GET /api/v1/recommend/similar/{room_id}?limit=5
```

**Tham số**:
- `room_id`: ID của phòng trọ cần tìm các phòng tương tự
- `limit`: Số lượng phòng tương tự muốn lấy (mặc định: 5)

**Mô tả**: Trả về danh sách phòng trọ tương tự với phòng đã chọn

**Phản hồi thành công**:
```json
{
  "success": true,
  "message": "Lấy danh sách phòng tương tự thành công",
  "data": [
    {
      "id": 456,
      "title": "Phòng trọ quận 1",
      // Các thông tin khác của phòng...
    },
    // ...
  ]
}
```

## Cơ chế hoạt động

### Mô hình Gợi ý dựa trên nội dung (Content-based)

Mô hình này gợi ý các phòng tương tự dựa trên đặc điểm của phòng:
- Vị trí địa lý (tỉnh/thành phố, quận/huyện)
- Giá cả, diện tích
- Tiện nghi
- Đối tượng thuê phù hợp
- Các khu vực xung quanh
- Mô tả phòng

Khi gọi API `/similar/{room_id}`, hệ thống sẽ tìm các phòng có đặc điểm tương tự nhất với phòng có ID là `room_id`.

### Mô hình Gợi ý dựa trên hành vi (Collaborative)

Mô hình này theo dõi hành vi người dùng (xem và lưu phòng) và gợi ý phòng dựa trên:
- Phòng mà người dùng đã xem/lưu
- Phòng mà những người dùng tương tự đã xem/lưu

Dữ liệu hành vi được lưu trong Redis để tối ưu hiệu suất.

### Quá trình Huấn luyện mô hình

1. Lấy dữ liệu phòng từ `room-service`
2. Tiền xử lý dữ liệu (chuẩn hóa, chuyển đổi định dạng)
3. Huấn luyện mô hình content-based
4. Lưu mô hình đã huấn luyện

Mô hình collaborative được cập nhật liên tục khi có dữ liệu mới từ hành vi người dùng.

## Xử lý lỗi

### 1. Lỗi kết nối Config Server

Kiểm tra:
- Config Server đã khởi động
- URL Config Server đúng (mặc định: http://localhost:8888)
- Tên ứng dụng đúng (recommend-service)

### 2. Lỗi kết nối Redis

Kiểm tra:
- Redis server đã khởi động
- Thông tin kết nối (host, port, password) chính xác trong cấu hình

### 3. Lỗi huấn luyện mô hình

Kiểm tra:
- Kết nối với room-service hoạt động tốt
- Định dạng dữ liệu từ room-service đúng cấu trúc
- Đủ dung lượng lưu trữ cho mô hình

### 4. Lỗi khi gọi API

Kiểm tra:
- Eureka Server đã khởi động
- room-service đã đăng ký với Eureka
- API Gateway định tuyến chính xác

## Tác giả và Liên hệ

Dịch vụ này được phát triển như một phần của dự án TroTot - Hệ thống tìm kiếm phòng trọ thông minh.

Nếu có vấn đề hoặc cần hỗ trợ, vui lòng liên hệ Email: thanhnhutcu@gmail.com.