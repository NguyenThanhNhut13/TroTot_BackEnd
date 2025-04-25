import logging

logger = logging.getLogger(__name__)

# Biến toàn cục lưu client Eureka
eureka_client_instance = None

def set_eureka_client(client):
    global eureka_client_instance
    eureka_client_instance = client
    logger.info("Đã lưu eureka_client_instance")

def get_eureka_client():
    if eureka_client_instance is None:
        logger.error("eureka_client_instance chưa được khởi tạo")
        raise ValueError("Eureka client chưa được khởi tạo")
    return eureka_client_instance