# 1) Forward Geocoding (Chuyển địa chỉ thành tọa độ)
- https://nominatim.openstreetmap.org/search?format=json&q={address}
- Ví dụ: curl "https://nominatim.openstreetmap.org/search?format=json&q=81 Nguyễn Cửu Vân, Bình Thạnh, Hồ Chí Minh"
- Response mẫu:
  [
    {
      "lat": "10.7996",
      "lon": "106.7102",
      "display_name": "81 Nguyễn Cửu Vân, Phường 17, Bình Thạnh, Hồ Chí Minh, Việt Nam"
    }
  ]
# 2) Reverse Geocoding (Chuyển tọa độ thành địa chỉ)
- https://nominatim.openstreetmap.org/reverse?format=json&lat={latitude}&lon={longitude}
- Ví dụ: curl "https://nominatim.openstreetmap.org/reverse?format=json&lat=10.7996&lon=106.7102"
- Response mẫu
  {
     "place_id": "123456",
     "lat": "10.7996",
     "lon": "106.7102",
     "display_name": "81 Nguyễn Cửu Vân, Phường 17, Bình Thạnh, Hồ Chí Minh, Việt Nam"
  }


