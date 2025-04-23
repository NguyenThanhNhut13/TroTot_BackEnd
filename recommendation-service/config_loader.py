# import requests

# def load_config(service_name="recommendation-service"):
#     url = f"http://localhost:8888/{service_name}/default"
#     try:
#         response = requests.get(url)
#         config = response.json()

#         # lấy thông tin từ propertySources
#         properties = config['propertySources'][0]['source']
#         return {
#             "port": properties.get("server.port", 8100),
#             "eureka_url": properties.get("eureka.client.service-url.defaultZone", "http://localhost:8761/eureka")
#         }

#     except Exception as e:
#         print("❌ Không thể lấy cấu hình từ Config Server:", e)
#         return {
#             "port": 8100,
#             "eureka_url": "http://localhost:8761/eureka"
#         }
