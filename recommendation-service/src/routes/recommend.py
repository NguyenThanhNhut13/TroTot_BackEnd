from fastapi import APIRouter, HTTPException
from src.models.recommend_model import RecommendModel
from src.clients.service_client import ServiceClient
from src.clients.config_client import load_config_from_config_server
from src.clients.eureka_config import get_eureka_client
import logging

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/v1/recommend", tags=["recommend"])

# Đọc cấu hình
try:
    config = load_config_from_config_server(
        config_server_url="http://localhost:8888",
        app_name="recommend-service"
    )
except Exception as e:
    logger.error(f"Lỗi khi đọc cấu hình: {str(e)}")
    raise

# Khởi tạo mô hình
model = RecommendModel()

async def get_rooms_data():
    try:
        room_client = ServiceClient(
            eureka_client=get_eureka_client(),
            app_name="room-service",
            gateway_app_name="api-gateway"
        )
        
        data = await room_client.call_service("/api/v1/rooms/export")
        
        if not isinstance(data, dict) or 'success' not in data:
            raise ValueError("Phản hồi từ room-service không đúng định dạng: thiếu trường 'success'")
        if not data['success']:
            raise ValueError(f"room-service trả về lỗi: {data.get('message', 'Không có thông báo lỗi')}")
        if 'data' not in data:
            raise ValueError("Phản hồi từ room-service thiếu trường 'data'")
        
        rooms = data['data']
        if not isinstance(rooms, list):
            raise ValueError("Trường 'data' phải là danh sách phòng trọ")
        if not rooms:
            raise ValueError("Danh sách phòng trọ rỗng")
        
        for room in rooms:
            room['roomId'] = room.get('id', 0)
            room['amenities'] = ', '.join(room['amenities']) if room.get('amenities') else 'unknown'
            room['targetAudiences'] = ', '.join(room['targetAudiences']) if room.get('targetAudiences') else 'unknown'
            room['surroundingAreas'] = ', '.join(room['surroundingAreas']) if room.get('surroundingAreas') else 'unknown'
            room['description'] = room.get('description', room.get('title', ''))
        
        await room_client.close()
        return rooms
    except ValueError as e:
        logger.error(f"Lỗi dữ liệu: {str(e)}")
        raise HTTPException(status_code=400, detail=str(e))
    except HTTPException as e:
        raise e
    except Exception as e:
        logger.error(f"Lỗi khi lấy dữ liệu phòng trọ: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Lỗi khi lấy dữ liệu phòng trọ: {str(e)}")

@router.post("/train")
async def train():
    try:
        rooms_data = await get_rooms_data()
        model.train(rooms_data)
        logger.info("Mô hình đã được huấn luyện thành công")
        return {"message": "Mô hình đã được huấn luyện thành công"}
    except ValueError as e:
        raise HTTPException(status_code=400, detail=f"Lỗi dữ liệu: {str(e)}")
    except HTTPException as e:
        raise e
    except Exception as e:
        logger.error(f"Lỗi khi huấn luyện mô hình: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Lỗi không xác định khi huấn luyện mô hình: {str(e)}")

@router.get("/similar/{room_id}")
async def get_similar_rooms(room_id: int, limit: int = 5):
    try:
        model.load_models()
        similar_rooms = model.get_similar_rooms(room_id, limit)
        logger.info(f"Lấy danh sách phòng tương tự cho room_id={room_id}: {similar_rooms}")
        return {"similar_rooms": similar_rooms}
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except HTTPException as e:
        raise e
    except Exception as e:
        logger.error(f"Lỗi khi lấy phòng tương tự: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Lỗi không xác định khi lấy phòng tương tự: {str(e)}")