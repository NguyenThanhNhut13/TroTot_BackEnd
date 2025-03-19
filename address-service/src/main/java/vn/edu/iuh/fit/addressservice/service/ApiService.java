package vn.edu.iuh.fit.addressservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.fit.addressservice.dto.ProvinceDTO;
import vn.edu.iuh.fit.addressservice.dto.DistrictDTO;
import vn.edu.iuh.fit.addressservice.dto.WardDTO;

import java.util.Arrays;
import java.util.List;

@Service
public class ApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<ProvinceDTO> fetchProvinces() {
        String url = "https://vn-public-apis.fpo.vn/provinces/getAll?limit=-1";
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path("data").path("data");
            return Arrays.asList(objectMapper.treeToValue(dataNode, ProvinceDTO[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<DistrictDTO> fetchDistricts() {
        String url = "https://vn-public-apis.fpo.vn/districts/getAll?limit=-1";
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path("data").path("data");
            return Arrays.asList(objectMapper.treeToValue(dataNode, DistrictDTO[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<WardDTO> fetchWards() {
        String url = "https://vn-public-apis.fpo.vn/wards/getAll?limit=-1";
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path("data").path("data");
            return Arrays.asList(objectMapper.treeToValue(dataNode, WardDTO[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
