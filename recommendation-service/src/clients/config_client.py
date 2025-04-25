import httpx
import logging

logger = logging.getLogger(__name__)

def load_config_from_config_server(config_server_url: str, app_name: str, profile: str = "default"):
    try:
        logger.info(f"Đang lấy cấu hình từ {config_server_url}/{app_name}/{profile}")
        response = httpx.get(f"{config_server_url}/{app_name}/{profile}")
        response.raise_for_status()
        config_data = response.json()
        for source in config_data.get("propertySources", []):
            if source["name"].endswith(f"{app_name}.yml"):
                logger.info(f"Tìm thấy cấu hình cho {app_name}")
                return source["source"]
        raise ValueError(f"Không tìm thấy cấu hình cho {app_name}")
    except httpx.HTTPError as e:
        logger.error(f"Lỗi khi lấy cấu hình từ Config Server: {str(e)}")
        raise ValueError(f"Lỗi khi lấy cấu hình từ Config Server: {str(e)}")