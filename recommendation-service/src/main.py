import logging
import asyncio
from fastapi import FastAPI
from src.routes import recommend
from src.clients.config_client import load_config_from_config_server
from src.clients.eureka_config import set_eureka_client
import py_eureka_client.eureka_client as eureka_client

# Cấu hình logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

# Đọc cấu hình từ Config Server
try:
    config = load_config_from_config_server(
        config_server_url="http://localhost:8888",
        app_name="recommend-service"
    )
    PORT = config.get("server.port", 5000)
    EUREKA_SERVER = config.get("eureka.client.service-url.defaultZone", "http://localhost:8761/eureka")
    APP_NAME = config.get("application.name", "recommend-service")
    logger.info(f"Đã đọc cấu hình: port={PORT}, eureka={EUREKA_SERVER}, app_name={APP_NAME}")
except Exception as e:
    logger.error(f"Lỗi khi đọc cấu hình từ Config Server: {str(e)}")
    raise

# Đăng ký với Eureka bất đồng bộ
async def register_with_eureka():
    try:
        logger.info(f"Đăng ký với Eureka: app_name={APP_NAME}, port={PORT}, eureka_server={EUREKA_SERVER}")
        client = await eureka_client.init_async(
            eureka_server=EUREKA_SERVER,
            app_name=APP_NAME,
            instance_port=PORT,
            instance_ip="127.0.0.1",  # Dùng IP cố định để tránh lỗi DNS
            instance_host="localhost",
            renewal_interval_in_secs=30,
            duration_in_secs=90
        )
        set_eureka_client(client)  # Lưu client vào eureka_config
        logger.info(f"Đăng ký {APP_NAME} với Eureka thành công")
    except Exception as e:
        logger.error(f"Lỗi khi đăng ký với Eureka: {str(e)}")
        raise

# Chạy đăng ký khi khởi động ứng dụng
@app.on_event("startup")
async def startup_event():
    await register_with_eureka()

# Gắn routes
app.include_router(recommend.router)

@app.get("/health")
async def health_check():
    return {"status": "UP"}