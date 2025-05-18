import httpx
import logging
from typing import Optional, Dict, Any
from fastapi import HTTPException
import os

logger = logging.getLogger(__name__)

class ServiceClient:
    def __init__(self, eureka_client, app_name: str, gateway_app_name: str):
        self.eureka_client = eureka_client
        self.app_name = app_name
        self.gateway_app_name = gateway_app_name
        self.client = httpx.AsyncClient(timeout=50)  # Timeout 30 giây
        self.base_url = self._discover_gateway()

    def _discover_gateway(self) -> str:
        logger.info(f"Khám phá API Gateway: {self.gateway_app_name}")
        # Sử dụng URL đã xác nhận từ bạn
        gateway_url = os.getenv("GATEWAY_URL", "http://localhost:8222")  # Đổi từ 192.168.100.1:8222 thành localhost:8222
        logger.info(f"Tìm thấy API Gateway: {gateway_url}")
        return gateway_url.rstrip("/")

    async def call_service(self, endpoint: str, method: str = "GET", json: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        try:
            url = f"{self.base_url}{endpoint}"
            logger.info(f"Gọi API: {method} {url}")

            if method.upper() == "GET":
                response = await self.client.get(url)
            elif method.upper() == "POST":
                logger.info(f"Body gửi đi: {json}")
                response = await self.client.post(url, json=json)
            else:
                raise ValueError(f"Phương thức {method} chưa được hỗ trợ")

            # Kiểm tra mã trạng thái HTTP
            response.raise_for_status()

            # Thử parse JSON
            try:
                return response.json()
            except ValueError as e:
                logger.error(f"Lỗi khi parse JSON từ phản hồi của {self.app_name}: {str(e)}")
                raise HTTPException(
                    status_code=500,
                    detail=f"Lỗi khi parse JSON từ phản hồi của {self.app_name}: {str(e)}"
                )

        except httpx.RequestError as e:
            logger.error(f"Lỗi kết nối khi gọi {self.app_name}: {str(e)}")
            raise HTTPException(status_code=500, detail=f"Lỗi kết nối khi gọi {self.app_name}: {str(e)}")
        except httpx.HTTPStatusError as e:
            logger.error(f"Lỗi HTTP khi gọi {self.app_name}: {e.response.status_code} - {e.response.text}")
            raise HTTPException(
                status_code=500,
                detail=f"Lỗi HTTP khi gọi {self.app_name}: {e.response.status_code} - {e.response.text}"
            )
        except ValueError as e:
            logger.error(f"Lỗi không xác định khi gọi {self.app_name}: {str(e)}")
            raise HTTPException(status_code=500, detail=f"Lỗi khi gọi {self.app_name}: {str(e)}")
        except Exception as e:
            logger.error(f"Lỗi không xác định khi gọi {self.app_name}: {str(e)}")
            raise HTTPException(status_code=500, detail=f"Lỗi khi gọi {self.app_name}: {str(e)}")

    async def close(self):
        await self.client.aclose()