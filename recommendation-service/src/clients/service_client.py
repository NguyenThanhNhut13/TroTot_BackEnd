import httpx
from fastapi import HTTPException
import logging

logger = logging.getLogger(__name__)

class ServiceClient:
    def __init__(self, eureka_client, app_name: str, gateway_app_name: str):
        self.eureka_client = eureka_client
        self.app_name = app_name.lower()
        self.gateway_app_name = gateway_app_name.lower()
        self.client = httpx.AsyncClient(timeout=30.0)  # Thêm timeout
        logger.info(f"Khởi tạo ServiceClient: app_name={self.app_name}, gateway={self.gateway_app_name}")

    async def discover_gateway(self):
        try:
            logger.info(f"Khám phá API Gateway: {self.gateway_app_name}")
            applications = self.eureka_client.applications
            app = applications.get_application(self.gateway_app_name)
            if not app or not app.instances:
                raise ValueError(f"Không tìm thấy instance cho {self.gateway_app_name}")
            instance = app.instances[0]
            gateway_url = f"http://{instance.ipAddr}:{instance.port.port}"
            logger.info(f"Tìm thấy API Gateway: {gateway_url}")
            return gateway_url
        except Exception as e:
            logger.error(f"Lỗi khi khám phá API Gateway: {str(e)}", exc_info=True)
            raise ValueError(f"Lỗi khi khám phá API Gateway: {str(e)}")

    async def call_service(self, endpoint: str, method: str = "GET", **kwargs):
        try:
            gateway_url = await self.discover_gateway()
            service_url = f"{gateway_url}/{endpoint.lstrip('/')}"
            logger.info(f"Gọi API: {method} {service_url}")
            headers = kwargs.get("headers", {})
            # Thêm token xác thực (thay <your-token> bằng token thực tế hoặc logic lấy token)
            headers["Authorization"] = "Bearer <your-token>"  # Cần thay bằng token thực tế
            kwargs["headers"] = headers
            if method.upper() == "GET":
                response = await self.client.get(service_url, **kwargs)
            else:
                raise ValueError(f"Phương thức {method} chưa được hỗ trợ")
            response.raise_for_status()
            return response.json()
        except httpx.HTTPStatusError as e:
            logger.error(f"Lỗi HTTP khi gọi {self.app_name}: {str(e)}", exc_info=True)
            raise HTTPException(status_code=e.response.status_code, detail=f"Lỗi khi gọi {self.app_name}: {str(e)}")
        except httpx.RequestError as e:
            logger.error(f"Lỗi mạng khi gọi {self.app_name}: {str(e)}", exc_info=True)
            raise HTTPException(status_code=500, detail=f"Lỗi mạng khi gọi {self.app_name}: {str(e)}")
        except Exception as e:
            logger.error(f"Lỗi không xác định khi gọi {self.app_name}: {str(e)}", exc_info=True)
            raise HTTPException(status_code=500, detail=f"Lỗi khi gọi {self.app_name}: {str(e)}")

    async def close(self):
        await self.client.aclose()