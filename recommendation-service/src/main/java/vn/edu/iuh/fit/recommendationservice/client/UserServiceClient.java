package vn.edu.iuh.fit.recommendationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.iuh.fit.recommendationservice.dto.UserDTO;

@FeignClient(name = "user-service", url = "http://localhost:8090")
public interface UserServiceClient {
    @GetMapping("/api/v1/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
