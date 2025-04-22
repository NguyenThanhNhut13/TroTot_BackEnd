/*
 * @ (#) RoomClient.java       1.0     22/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 22/04/2025
 * @version:    1.0
 */

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.edu.iuh.fit.userservice.config.FeignClientConfig;
import vn.edu.iuh.fit.userservice.model.dto.reponse.BaseResponse;
import vn.edu.iuh.fit.userservice.model.dto.reponse.RoomListResponse;

import java.util.List;

@FeignClient(name = "room-service", configuration = FeignClientConfig.class)
public interface RoomClient {
    @GetMapping("/api/v1/rooms/{id}/exists")
    ResponseEntity<BaseResponse<Boolean>> checkRoomExists(@PathVariable Long id);

    @PostMapping("/api/v1/rooms/{id}/exists")
    ResponseEntity<BaseResponse<List<RoomListResponse>>> findByIds(@RequestBody List<Long> ids);
}
