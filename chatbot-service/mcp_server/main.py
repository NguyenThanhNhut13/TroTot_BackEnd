from fastapi import FastAPI, HTTPException
from typing import List
import httpx
from loguru import logger
from contextlib import asynccontextmanager # Để quản lý lifespan events

from config import ROOMSERVICE_API_BASE_URL
from schemas import SearchRoomsArgs, ToolResponseData, TOOL_DEFINITION

# --- Khai báo một context manager cho lifespan events ---
# Đây là nơi bạn khởi tạo và đóng tài nguyên (như httpx.AsyncClient)
@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Context manager cho các sự kiện khởi động và tắt ứng dụng FastAPI.
    Khởi tạo httpx.AsyncClient khi app khởi động và đóng khi app tắt.
    """
    logger.info("Starting up MCP Server...")
    # Khởi tạo AsyncClient một lần
    app.state.httpx_client = httpx.AsyncClient()
    logger.info("httpx.AsyncClient initialized.")
    yield # Ứng dụng sẽ chạy ở đây
    # Đoạn code này sẽ chạy khi ứng dụng tắt
    logger.info("Shutting down MCP Server...")
    await app.state.httpx_client.aclose() # Đóng client một cách gọn gàng
    logger.info("httpx.AsyncClient closed.")

# Truyền lifespan vào FastAPI constructor
app = FastAPI(
    title="MCP Server for Room Search",
    description="Server trung gian để kết nối Gemini với Roomservice API.",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    lifespan=lifespan # Gắn lifespan context manager vào ứng dụng
)

@app.get("/health", summary="Health check endpoint")
async def health_check():
    """Kiểm tra trạng thái hoạt động của MCP Server."""
    return {"status": "MCP Server is running!"}

@app.get("/tool_definitions", response_model=List[dict], summary="Get tool definitions")
async def get_tool_definitions():
    """Cung cấp định nghĩa các tool mà MCP Server hỗ trợ cho Backend Chatbot."""
    logger.info("Serving tool definitions.")
    return [TOOL_DEFINITION]

@app.post("/tools/search_rooms", response_model=ToolResponseData, summary="Search rooms tool")
async def search_rooms_tool_endpoint(args: SearchRoomsArgs):
    """
    Endpoint mà Backend Chatbot sẽ gọi khi Gemini yêu cầu sử dụng tool 'search_rooms'.
    Nhận các tham số từ Gemini, gọi roomservice API, và trả về kết quả.
    """
    logger.info(f"Received request to call tool 'search_rooms' with arguments: {args.dict()}")

    # Chuẩn bị tham số cho roomservice API
    params = {}
    if args.street is not None:
        params['street'] = args.street
    if args.district is not None:
        params['district'] = args.district
    if args.city is not None:
        params['city'] = args.city
    if args.min_price is not None:
        params['minPrice'] = args.min_price
    if args.max_price is not None:
        params['maxPrice'] = args.max_price
    if args.area_range is not None:
        params['areaRange'] = args.area_range
    if args.room_type is not None:
        params['roomType'] = args.room_type
    if args.amenities is not None:
        params['amenities'] = ",".join(args.amenities)
    if args.environment is not None:
        params['environment'] = ",".join(args.environment)
    if args.target_audience is not None:
        params['targetAudience'] = ",".join(args.target_audience)
    if args.has_video_review is not None:
        params['hasVideoReview'] = args.has_video_review

    roomservice_url = f"{ROOMSERVICE_API_BASE_URL}/api/v1/rooms/search"
    logger.info(f"Calling roomservice API: {roomservice_url} with params: {params}")

    try:
        # Sử dụng httpx_client đã được khởi tạo sẵn trong app.state
        response = await app.state.httpx_client.get(roomservice_url, params=params, timeout=10.0)
        response.raise_for_status()
        room_data = response.json()
        logger.info(f"Received {len(room_data)} rooms from roomservice.")

        return ToolResponseData(success=True, data=room_data)

    except httpx.RequestError as e:
        logger.error(f"Error calling roomservice API: {e}")
        raise HTTPException(
            status_code=500,
            detail=ToolResponseData(success=False, error=f"Không thể kết nối đến dịch vụ tìm phòng: {e}").dict()
        )
    except httpx.HTTPStatusError as e:
        logger.error(f"Roomservice API responded with an error: {e.response.status_code} - {e.response.text}")
        raise HTTPException(
            status_code=e.response.status_code,
            detail=ToolResponseData(success=False, error=f"Dịch vụ tìm phòng phản hồi lỗi: {e.response.status_code}").dict()
        )
    except Exception as e:
        logger.error(f"Unknown error in MCP Server: {e}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail=ToolResponseData(success=False, error=f"Lỗi nội bộ server: {e}").dict()
        )