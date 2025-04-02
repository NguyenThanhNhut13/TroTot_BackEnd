//package vn.edu.iuh.fit.addressservice.seeder;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import vn.edu.iuh.fit.addressservice.dto.DistrictDTO;
//import vn.edu.iuh.fit.addressservice.dto.ProvinceDTO;
//import vn.edu.iuh.fit.addressservice.dto.WardDTO;
//import vn.edu.iuh.fit.addressservice.entity.District;
//import vn.edu.iuh.fit.addressservice.entity.Province;
//import vn.edu.iuh.fit.addressservice.entity.Ward;
//import vn.edu.iuh.fit.addressservice.repository.DistrictRepository;
//import vn.edu.iuh.fit.addressservice.repository.ProvinceRepository;
//import vn.edu.iuh.fit.addressservice.repository.WardRepository;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class AddressSeeder {
//    private final ProvinceRepository provinceRepository;
//    private final DistrictRepository districtRepository;
//    private final WardRepository wardRepository;
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @PostConstruct
//    public void seedDatabase() {
//        if (provinceRepository.count() == 0) {
//            loadProvinces();
//        }
//        if (districtRepository.count() == 0) {
//            loadDistricts();
//        }
//        if (wardRepository.count() == 0) {
//            loadWards();
//        }
//    }
//
//    private void loadProvinces() {
//        String url = "https://vn-public-apis.fpo.vn/provinces/getAll?limit=-1";
//        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
//
//        if (response != null && response.has("data") && response.get("data").has("data")) {
//            List<ProvinceDTO> provinceDTOs = objectMapper.convertValue(
//                    response.get("data").get("data"), new TypeReference<List<ProvinceDTO>>() {}
//            );
//
//            List<Province> provinces = provinceDTOs.stream().map(dto -> new Province(
//                    dto.getId(), dto.getCode(), dto.getName(), dto.getSlug(),
//                    dto.getType(), dto.getNameWithType(), dto.isDeleted(), null
//            )).toList();
//
//            provinceRepository.saveAll(provinces);
//            System.out.println("✅ Đã tải xong danh sách tỉnh/thành phố!");
//        }
//    }
//
//    private void loadDistricts() {
//        String url = "https://vn-public-apis.fpo.vn/districts/getAll?limit=-1";
//        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
//
//        if (response != null && response.has("data") && response.get("data").has("data")) {
//            List<DistrictDTO> districtDTOs = objectMapper.convertValue(
//                    response.get("data").get("data"), new TypeReference<List<DistrictDTO>>() {}
//            );
//
//            // Tạo Map ánh xạ provinceCode -> Province Entity
//            Map<String, Province> provinceMap = new HashMap<>();
//            provinceRepository.findAll().forEach(province -> provinceMap.put(province.getCode(), province));
//
//            List<District> districts = districtDTOs.stream().map(dto -> {
//                Province province = provinceMap.get(dto.getProvinceCode());
//                return new District(
//                        dto.getId(), dto.getCode(), dto.getName(), dto.getSlug(), dto.getType(),
//                        dto.getNameWithType(), dto.getPath(), dto.getPathWithType(),
//                        dto.isDeleted(), province, null
//                );
//            }).toList();
//
//            districtRepository.saveAll(districts);
//            System.out.println("✅ Đã tải xong danh sách quận/huyện!");
//        }
//    }
//
//    private void loadWards() {
//        String url = "https://vn-public-apis.fpo.vn/wards/getAll?limit=-1";
//        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
//
//        if (response != null && response.has("data") && response.get("data").has("data")) {
//            List<WardDTO> wardDTOs = objectMapper.convertValue(
//                    response.get("data").get("data"), new TypeReference<List<WardDTO>>() {}
//            );
//
//            // Tạo Map ánh xạ districtCode -> District Entity
//            Map<String, District> districtMap = new HashMap<>();
//            districtRepository.findAll().forEach(district -> districtMap.put(district.getCode(), district));
//
//            List<Ward> wards = wardDTOs.stream().map(dto -> {
//                District district = districtMap.get(dto.getDistrictCode());
//                return new Ward(
//                        dto.getId(), dto.getCode(), dto.getName(), dto.getSlug(), dto.getType(),
//                        dto.getNameWithType(), dto.getPath(), dto.getPathWithType(),
//                        dto.isDeleted(), district
//                );
//            }).toList();
//
//            wardRepository.saveAll(wards);
//            System.out.println("✅ Đã tải xong danh sách phường/xã!");
//        }
//    }
//}
