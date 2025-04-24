from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from src.models.recommend_model import RecommendModel
from src.models.user_behavior_model import UserBehaviorModel
from src.clients.service_client import ServiceClient
from src.clients.config_client import load_config_from_config_server
from src.clients.eureka_config import get_eureka_client
from redis.asyncio import Redis
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

# Khởi tạo Redis client
redis_client = Redis(
    host=config.get("redis.host", "localhost"),
    port=config.get("redis.port", 6379),
    password=config.get("redis.password", ""),
    # ssl=config.get("redis.ssl", True)
)

# Khởi tạo model và client (Lazy Initialization)
content_model = None
behavior_model = None
room_client = None

def get_content_model():
    global content_model
    if content_model is None:
        content_model = RecommendModel()
    return content_model

def get_behavior_model():
    global behavior_model
    if behavior_model is None:
        behavior_model = UserBehaviorModel(redis_client)
    return behavior_model

def get_room_client():
    global room_client
    if room_client is None:
        room_client = ServiceClient(
            eureka_client=get_eureka_client(),
            app_name="room-service",
            gateway_app_name="api-gateway"
        )
    return room_client

# Hàm lấy dữ liệu phòng từ room-service
async def get_rooms_data():
    try:
        room_client = get_room_client()
        
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
        
        return rooms
    except ValueError as e:
        logger.error(f"Lỗi dữ liệu: {str(e)}")
        raise HTTPException(status_code=400, detail=str(e))
    except HTTPException as e:
        raise e
    except Exception as e:
        logger.error(f"Lỗi khi lấy dữ liệu phòng trọ: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Lỗi khi lấy dữ liệu phòng trọ: {str(e)}")

# Model cho request body của track/view
class TrackViewRequest(BaseModel):
    user_id: int
    room_id: int

@router.post("/track/view")
async def track_view(request: TrackViewRequest):
    try:
        user_id = request.user_id
        room_id = request.room_id
        await redis_client.hset(f"user:{user_id}:viewed_rooms", room_id, 1)
        await redis_client.expire(f"user:{user_id}:viewed_rooms", config.get("redis.expire_time", 86400))
        await redis_client.zincrby("room_views", 1, room_id)
        logger.info(f"Đã theo dõi xem phòng {room_id} cho user {user_id}")
        return {
            "success": True,
            "message": "Đã theo dõi hành vi xem phòng",
            "data": None
        }
    except Exception as e:
        logger.error(f"Lỗi khi theo dõi xem phòng: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Lỗi khi theo dõi xem phòng: {str(e)}")

# Model cho request body của track/save
class TrackSaveRequest(BaseModel):
    user_id: int
    room_id: int

@router.post("/track/save")
async def track_save(request: TrackSaveRequest):
    try:
        user_id = request.user_id
        room_id = request.room_id
        await redis_client.zincrby("room_saves", 1, room_id)
        logger.info(f"Đã ghi nhận lưu phòng {room_id} cho user {user_id}")
        return {
            "success": True,
            "message": "Đã ghi nhận hành vi lưu phòng",
            "data": None
        }
    except Exception as e:
        logger.error(f"Lỗi khi ghi nhận lưu phòng: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Lỗi khi ghi nhận lưu phòng: {str(e)}")

# API gợi ý phòng cho user
@router.get("/user/{user_id}")
async def recommend_user(user_id: int, limit: int = 5):
    try:
        # Lấy dữ liệu phòng trước
        rooms_data = await get_rooms_data()
        
        # Khởi tạo model và truyền dữ liệu phòng nếu cần
        behavior_model = get_behavior_model()
        
        # Lấy danh sách ID phòng được gợi ý
        recommended_room_ids = await behavior_model.recommend_user_based(
            user_id=user_id,
            limit=limit
        )
        
        # Gọi room-service để lấy chi tiết các phòng
        room_client = get_room_client()
        if recommended_room_ids:
            response = await room_client.call_service(
                endpoint="/api/v1/rooms/bulk",
                method="POST",
                json=recommended_room_ids
            )
            if not response.get("success"):
                logger.error(f"room-service trả về lỗi: {response.get('message', 'Không có thông báo lỗi')}")
                return {
                    "success": False,
                    "message": "Không thể lấy chi tiết phòng từ room-service",
                    "data": []
                }
        else:
            response = {
                "success": True,
                "message": "Không có phòng nào được gợi ý",
                "data": []
            }

        logger.info(f"Gợi ý phòng cho user_id={user_id}: {recommended_room_ids}")
        return response
    except Exception as e:
        logger.error(f"Lỗi khi gợi ý cho user: {str(e)}")
        return {
            "success": False,
            "message": f"Lỗi khi gợi ý: {str(e)}",
            "data": []
        }

# API lấy danh sách phòng tương tự
@router.get("/similar/{room_id}")
async def get_similar_rooms(room_id: int, limit: int = 5):
    try:
        # Lấy dữ liệu phòng trước
        rooms_data = await get_rooms_data()
        
        # Khởi tạo model và truyền dữ liệu phòng
        content_model = get_content_model()
        content_model.load_models()
        
        # Lấy danh sách ID phòng tương tự
        similar_room_ids = content_model.get_similar_rooms(room_id, limit)
        
        # Gọi room-service để lấy chi tiết các phòng
        room_client = get_room_client()
        if similar_room_ids:
            response = await room_client.call_service(
                endpoint="/api/v1/rooms/bulk",
                method="POST",
                json=similar_room_ids
            )
            if not response.get("success"):
                logger.error(f"room-service trả về lỗi: {response.get('message', 'Không có thông báo lỗi')}")
                return {
                    "success": False,
                    "message": "Không thể lấy chi tiết phòng từ room-service",
                    "data": []
                }
        else:
            response = {
                "success": True,
                "message": "Không có phòng tương tự nào",
                "data": []
            }

        logger.info(f"Lấy danh sách phòng tương tự cho room_id={room_id}: {similar_room_ids}")
        return response
    except ValueError as e:
        return {
            "success": False,
            "message": str(e),
            "data": []
        }
    except Exception as e:
        logger.error(f"Lỗi khi lấy phòng tương tự: {str(e)}")
        return {
            "success": False,
            "message": f"Lỗi không xác định khi lấy phòng tương tự: {str(e)}",
            "data": []
        }