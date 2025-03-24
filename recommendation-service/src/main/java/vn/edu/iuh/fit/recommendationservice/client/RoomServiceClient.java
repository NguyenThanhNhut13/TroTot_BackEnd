package vn.edu.iuh.fit.recommendationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.iuh.fit.recommendationservice.dto.RoomDTO;

@FeignClient(name = "room-service", url = "http://localhost:8070")
public interface RoomServiceClient {
    @GetMapping("/api/v1/rooms/{id}")
    RoomDTO getRoomById(@PathVariable Long id);
}
