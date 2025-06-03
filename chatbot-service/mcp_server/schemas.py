from pydantic import BaseModel, Field
from typing import List, Optional, Any

# Model to get parameters from Gemini for search_rooms tool
class SearchRoomsArgs(BaseModel):
    street: Optional[str] = Field(None, description="Tên đường của phòng trọ.")
    district: Optional[str] = Field(None, description="Quận của phòng trọ.")
    city: Optional[str] = Field(None, description="Thành phố của phòng trọ.")

    minPrice: Optional[float] = Field(None, description="Giá tối thiểu của phòng trọ.")
    maxPrice: Optional[float] = Field(None, description="Giá tối đa của phòng trọ.")
    areaRange: Optional[str] = Field(
        None,
        description=(
            "Khoảng diện tích phòng trọ. Các giá trị hợp lệ:\n"
            "- 'UNDER_20': Dưới 20m²\n"
            "- '20_40': Từ 20m² đến 40m²\n"
            "- '40_60': Từ 40m² đến 60m²\n"
            "- '60_80': Từ 60m² đến 80m²\n"
            "- 'OVER_80': Trên 80m²\n"
            "Nếu không khớp giá trị nào, sẽ không lọc theo diện tích."
        )
    )
    roomType: Optional[str] = Field(None, description="Loại phòng trọ.")

    amenities: Optional[List[str]] = Field(None, description="Danh sách các tiện ích mong muốn.")
    environment: Optional[List[str]] = Field(None, description="Môi trường xung quanh phòng trọ.")
    targetAudience: Optional[List[str]] = Field(None, description="Đối tượng mục tiêu của phòng trọ.")

    hasVideoReview: Optional[bool] = Field(None, description="Phòng trọ có video review hay không.")

# Model cho phản hồi của tool search_rooms từ MCP Server
class ToolResponseData(BaseModel):
    success: bool
    data: Optional[List[Any]] = None  # Danh sách các đối tượng phòng trọ
    error: Optional[str] = None

# Định nghĩa Tool cho Gemini
# (Sẽ được sử dụng trong main.py và gửi cho Gemini)
TOOL_DEFINITION = {
    "name": "search_rooms",
    "description": "Tìm kiếm các phòng trọ có sẵn dựa trên các tiêu chí cụ thể như vị trí, giá cả, tiện ích và môi trường xung quanh.",
    "parameters": {
        "type": "object",
        "properties": {
            "street": {
                "type": "string",
                "description": "Tên đường của phòng trọ."
            },
            "district": {
                "type": "string",
                "description": "Quận của phòng trọ."
            },
            "city": {
                "type": "string",
                "description": "Thành phố của phòng trọ."
            },
            "minPrice": {
                "type": "number",
                "description": "Giá tối thiểu của phòng trọ."
            },
            "maxPrice": {
                "type": "number",
                "description": "Giá tối đa của phòng trọ."
            },
            "areaRange": {
                "type": "string",
                "enum": ["UNDER_20", "20_40", "40_60", "60_80", "OVER_80"],
                "description": (
                    "Khoảng diện tích phòng trọ. Các giá trị hợp lệ:\n"
                    "- 'UNDER_20': Dưới 20m²\n"
                    "- '20_40': Từ 20m² đến 40m²\n"
                    "- '40_60': Từ 40m² đến 60m²\n"
                    "- '60_80': Từ 60m² đến 80m²\n"
                    "- 'OVER_80': Trên 80m²\n"
                    "Nếu không khớp giá trị nào, sẽ không lọc theo diện tích."
                )
            },
            "roomType": {
                "type": "string",
                "description": "Loại phòng trọ."
            },
            "amenities": {
                "type": "array",
                "items": {"type": "string"},
                "description": "Danh sách các tiện ích mong muốn."
            },
            "environment": {
                "type": "array",
                "items": {"type": "string"},
                "description": "Môi trường xung quanh phòng trọ."
            },
            "targetAudience": {
                "type": "array",
                "items": {"type": "string"},
                "description": "Đối tượng mục tiêu của phòng trọ."
            },
            "hasVideoReview": {
                "type": ["boolean", None],
                "description": (
                    'Phòng trọ có video review hay không. '
                    'Nếu không cung cấp, sẽ không lọc theo tiêu chí này.'
                )
            }
        },
        "required": [] # Các tham số không bắt buộc
    }
}