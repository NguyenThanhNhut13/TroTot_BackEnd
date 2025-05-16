/*
 * @ (#) UserClient.java       1.0     20/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 20/04/2025
 * @version:    1.0
 */

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import vn.edu.iuh.fit.roomservice.config.FeignClientConfig;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;

@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface UserClient {

    @PostMapping("/api/v1/users/use-post-slot")
    ResponseEntity<BaseResponse<Integer>> usePostSlot();

}
