/*
 * @ (#) UserClient.java       1.0     09/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.apigateway.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 09/03/2025
 * @version:    1.0
 */

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${application.config.users-url}")
public interface UserClient {

    @GetMapping("/check-permission")
    Boolean checkPermission(
            @RequestParam("url") String url,
            @RequestParam("method") String method,
            @RequestParam("roles") List<String> roles);
}
