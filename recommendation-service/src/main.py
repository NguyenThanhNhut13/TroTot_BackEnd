import logging
import asyncio
from fastapi import FastAPI
from src.routes import recommend
from src.clients.config_client import load_config_from_config_server
from src.clients.eureka_config import set_eureka_client
import py_eureka_client.eureka_client as eureka_client
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.cron import CronTrigger
import httpx
from redis.asyncio import Redis
from fastapi.middleware.cors import CORSMiddleware
import os

# Cấu hình logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "https://fe-ktpm-le-tan-phats-projects-c916a385.vercel.app", "https://trotot-frontend.vercel.app"],  # Cho phép origin từ localhost:3000
    allow_credentials=True,
    allow_methods=["*"],  # Cho phép tất cả các phương thức (GET, POST, v.v.)
    allow_headers=["*"],  # Cho phép tất cả các header
)

# Đọc cấu hình từ Config Server
try:
    config_server_url = os.getenv("CONFIG_SERVER_URL", "http://localhost:8888")
    config = load_config_from_config_server(
        config_server_url=config_server_url,
        app_name="recommend-service"
    )
    PORT = config.get("server.port", 5000)
    EUREKA_SERVER = config.get("eureka.client.service-url.defaultZone", "http://localhost:8761/eureka")
    APP_NAME = config.get("application.name", "recommend-service")
    REDIS_HOST = config.get("redis.host", "localhost")
    REDIS_PORT = config.get("redis.port", 6379)
    REDIS_PASSWORD = config.get("redis.password", "")
    # REDIS_SSL = config.get("redis.ssl", True)
    REDIS_EXPIRE_TIME = config.get("redis.expire_time", 7*86400)
    logger.info(f"Đã đọc cấu hình: port={PORT}, eureka={EUREKA_SERVER}, app_name={APP_NAME}, redis={REDIS_HOST}:{REDIS_PORT}")
except Exception as e:
    logger.error(f"Lỗi khi đọc cấu hình từ Config Server: {str(e)}")
    raise

# Khởi tạo Redis client
redis_client = Redis(
    host=REDIS_HOST,
    port=REDIS_PORT,
    password=REDIS_PASSWORD,
    # ssl=REDIS_SSL
)

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
            renewal_interval_in_secs=60,
            duration_in_secs=90
        )
        set_eureka_client(client)  # Lưu client vào eureka_config
        logger.info(f"Đăng ký {APP_NAME} với Eureka thành công")
    except Exception as e:
        logger.error(f"Lỗi khi đăng ký với Eureka: {str(e)}")
        raise

RECOMMEND_API_URL = os.getenv("RECOMMEND_API_URL", "http://localhost:5000")
# Hàm gọi API train
async def trigger_train():
    try:
        async with httpx.AsyncClient() as client:
            response = await client.post(f"{RECOMMEND_API_URL}/api/v1/recommend/train")
            if response.status_code == 200:
                logger.info("Huấn luyện mô hình thành công")
            else:
                logger.error(f"Lỗi khi huấn luyện mô hình: {response.text}")
    except Exception as e:
        logger.error(f"Lỗi khi gọi API train: {str(e)}")

# Lên lịch huấn luyện lúc 12h đêm
def schedule_train_job():
    scheduler = BackgroundScheduler()
    scheduler.add_job(
        trigger_train,
        trigger=CronTrigger(hour=0, minute=0),  # 12h đêm hàng ngày
        id='train_model_job',
        replace_existing=True
    )
    scheduler.start()
    logger.info("Đã lên lịch huấn luyện mô hình lúc 12h đêm hàng ngày")

# Chạy đăng ký khi khởi động ứng dụng, Khởi động lịch khi ứng dụng chạy
@app.on_event("startup")
async def startup_event():
    await register_with_eureka()
    schedule_train_job()

# Gắn routes
app.include_router(recommend.router)

@app.get("/health")
async def health_check():
    return {"status": "UP"}

@app.get("/")
async def root():
    return {"message": "Recommend Service is running"}