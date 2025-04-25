import numpy as np
import logging
import time
import json
from redis.asyncio import Redis
from src.clients.service_client import ServiceClient
from src.clients.eureka_config import get_eureka_client
from sklearn.metrics.pairwise import cosine_similarity
from collections import Counter

logger = logging.getLogger(__name__)

class UserBehaviorModel:
    def __init__(self, redis_client: Redis):
        self.redis_client = redis_client
        self.service_client = ServiceClient(
            eureka_client=get_eureka_client(),
            app_name="user-service",
            gateway_app_name="api-gateway"
        )
        # Lưu trữ dữ liệu để tránh gọi lại nhiều lần
        self.all_user_data = None
        self.all_rooms = None
        self.user_vectors = None
        self.last_updated = None

    # Lấy danh sách phòng đã xem từ Redis
    async def get_viewed_rooms(self, user_id: int):
        try:
            viewed_rooms = await self.redis_client.hkeys(f"user:{user_id}:viewed_rooms")
            return [int(room_id) for room_id in viewed_rooms]
        except Exception as e:
            logger.error(f"Lỗi khi lấy viewed_rooms từ Redis: {str(e)}")
            return []

    # Lấy danh sách phòng đã lưu từ user-service
    async def get_favorite_rooms(self, user_id: int):
        try:
            data = await self.service_client.call_service(f"/api/v1/users/{user_id}/wish-list")
            if data.get("success"):
                # Truy cập trực tiếp roomIds từ data["data"]
                room_ids = data["data"]["roomIds"]
                # Đảm bảo room_ids là danh sách các số nguyên
                room_ids = [int(room_id) for room_id in room_ids]
                # Lưu wish-list vào Redis để sử dụng sau
                if room_ids:
                    await self.redis_client.set(f"user:{user_id}:wish_list", json.dumps(room_ids))
                return room_ids
            else:
                logger.error(f"user-service trả về lỗi: {data.get('message', 'Không có thông báo lỗi')}")
                return []
        except Exception as e:
            logger.error(f"Lỗi khi lấy favorite_rooms: {str(e)}")
            # Thử lấy từ Redis nếu có
            cached = await self.redis_client.get(f"user:{user_id}:wish_list")
            if cached:
                return json.loads(cached)  # Chuyển chuỗi thành list
            return []

    # Lấy tất cả wish-lists và viewed_rooms của các user
    async def get_all_user_data(self):
        try:
            # Kiểm tra xem dữ liệu đã được cache chưa
            if self.all_user_data and self.last_updated:
                # Giả sử cache hợp lệ trong 5 phút
                if time.time() - self.last_updated < 300:
                    return self.all_user_data

            data = await self.service_client.call_service("/api/v1/users/wish-list/all")
            if not data.get("success"):
                logger.error(f"user-service trả về lỗi: {data.get('message', 'Không có thông báo lỗi')}")
                return {}

            # data["data"] là danh sách các wish-list
            all_wish_lists = {str(item["userId"]): [int(room_id) for room_id in item["roomIds"]] for item in data["data"]}
            all_user_data = {}

            # Lấy viewed_rooms cho từng user
            for user_id in all_wish_lists.keys():
                viewed_rooms = await self.get_viewed_rooms(int(user_id))
                all_user_data[user_id] = {
                    "favorite_rooms": all_wish_lists[user_id],
                    "viewed_rooms": viewed_rooms
                }

            # Cache dữ liệu
            self.all_user_data = all_user_data
            self.last_updated = time.time()
            return all_user_data
        except Exception as e:
            logger.error(f"Lỗi khi lấy dữ liệu user: {str(e)}")
            return {}

    # Tạo vector hành vi và cache
    async def build_user_vectors(self):
        try:
            # Lấy dữ liệu của tất cả user
            all_user_data = await self.get_all_user_data()
            if not all_user_data:
                return

            # Tạo tập hợp tất cả các phòng
            all_rooms = set()
            for user_data in all_user_data.values():
                all_rooms.update(user_data["favorite_rooms"])
                all_rooms.update(user_data["viewed_rooms"])
            self.all_rooms = sorted(list(all_rooms))

            # Tạo vector hành vi cho mỗi user
            self.user_vectors = {}
            for uid, user_data in all_user_data.items():
                vector = []
                for room_id in self.all_rooms:
                    score = 0
                    if room_id in user_data["favorite_rooms"]:
                        score += 0.7  # Trọng số 70% cho favorite
                    if room_id in user_data["viewed_rooms"]:
                        score += 0.3  # Trọng số 30% cho viewed
                    vector.append(score)
                self.user_vectors[uid] = np.array(vector)

            logger.info("Đã tạo user vectors thành công")
        except Exception as e:
            logger.error(f"Lỗi khi tạo user vectors: {str(e)}")
            raise

    # Gợi ý phòng dựa trên hành vi (Collaborative Filtering)
    async def recommend_user_based(self, user_id: int, limit: int = 5):
        try:
            logger.info(f"Bắt đầu gợi ý dựa trên hành vi cho user_id: {user_id}")

            # Lấy dữ liệu của user hiện tại
            viewed_rooms = await self.get_viewed_rooms(user_id)
            favorite_rooms = await self.get_favorite_rooms(user_id)

            if not viewed_rooms and not favorite_rooms:
                return await self.get_popular_rooms(limit)

            # Cập nhật user vectors nếu cần
            await self.build_user_vectors()
            if not self.user_vectors or not self.all_rooms:
                return await self.get_popular_rooms(limit)

            # Vector hành vi của user hiện tại
            current_user_vector = np.zeros(len(self.all_rooms))
            for i, room_id in enumerate(self.all_rooms):
                if room_id in favorite_rooms:
                    current_user_vector[i] += 0.7
                if room_id in viewed_rooms:
                    current_user_vector[i] += 0.3

            # Tính độ tương đồng giữa user hiện tại và các user khác
            similarities = {}
            for uid, vector in self.user_vectors.items():
                if int(uid) == user_id:
                    continue
                sim = cosine_similarity([current_user_vector], [vector])[0][0]
                similarities[uid] = sim

            # Sắp xếp user theo độ tương đồng và lấy top 5
            top_similar_users = sorted(similarities.items(), key=lambda x: x[1], reverse=True)[:5]

            # Tính điểm cho các phòng
            room_scores = {}
            for uid, sim in top_similar_users:
                user_data = self.all_user_data[uid]
                for room_id in user_data["favorite_rooms"]:
                    if room_id not in favorite_rooms and room_id not in viewed_rooms:
                        room_scores[room_id] = room_scores.get(room_id, 0) + sim * 0.7
                for room_id in user_data["viewed_rooms"]:
                    if room_id not in favorite_rooms and room_id not in viewed_rooms:
                        room_scores[room_id] = room_scores.get(room_id, 0) + sim * 0.3

            # Sắp xếp và lấy top phòng
            recommended_rooms = sorted(room_scores.items(), key=lambda x: x[1], reverse=True)[:limit]
            recommended_rooms = [room_id for room_id, _ in recommended_rooms]

            logger.info(f"Phòng được gợi ý cho user_id {user_id}: {recommended_rooms}")
            return recommended_rooms
        except Exception as e:
            logger.error(f"Lỗi khi gợi ý: {str(e)}")
            return []

    # Lấy danh sách phòng phổ biến
    async def get_popular_rooms(self, limit=5):
        try:
            # Lấy dữ liệu từ Redis (room_views)
            views = await self.redis_client.zrange("room_views", 0, -1, withscores=True)

            # Lấy dữ liệu từ user-service (room_saves)
            data = await self.service_client.call_service("/api/v1/users/wish-list/all")
            if not data.get("success"):
                logger.error(f"user-service trả về lỗi: {data.get('message', 'Không có thông báo lỗi')}")
                saves = []
            else:
                # Đếm số lần lưu của mỗi phòng
                all_room_ids = []
                for user_data in data["data"]:
                    room_ids = user_data["roomIds"]
                    all_room_ids.extend(room_ids)
                saves = [(room_id, count) for room_id, count in Counter(all_room_ids).items()]

            # Tính điểm phổ biến: 0.5 * views + 0.5 * saves
            room_scores = {}
            for room_id, view_score in views:
                room_scores[int(room_id)] = 0.5 * view_score
            for room_id, save_score in saves:
                room_id = int(room_id)
                if room_id in room_scores:
                    room_scores[room_id] += 0.5 * save_score
                else:
                    room_scores[room_id] = 0.5 * save_score

            # Sắp xếp và lấy top phòng
            popular_rooms = sorted(room_scores.items(), key=lambda x: x[1], reverse=True)[:limit]
            return [room_id for room_id, _ in popular_rooms]
        except Exception as e:
            logger.error(f"Lỗi khi lấy phòng phổ biến: {str(e)}")
            return []