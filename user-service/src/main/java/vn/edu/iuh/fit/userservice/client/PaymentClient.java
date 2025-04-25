/*
 * @ (#) UserClient.java       1.0     20/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 20/04/2025
 * @version:    1.0
 */

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.edu.iuh.fit.userservice.config.FeignClientConfig;
import vn.edu.iuh.fit.userservice.model.dto.reponse.BaseResponse;
import vn.edu.iuh.fit.userservice.model.dto.request.DeductRequest;

@FeignClient(name = "payment-service", configuration = FeignClientConfig.class)
public interface PaymentClient {

    @Transactional
    @PostMapping("/api/v1/payments/deduct")
    BaseResponse<String> deduct(@RequestBody DeductRequest request);
}
