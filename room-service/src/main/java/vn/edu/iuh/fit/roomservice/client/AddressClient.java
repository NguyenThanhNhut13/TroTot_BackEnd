/*
 * @ (#) AddressClient.java       1.0     21/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 21/02/2025
 * @version:    1.0
 */

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.iuh.fit.roomservice.dto.AddressDTO;

import java.util.List;

@FeignClient(name = "address-service", url = "${application.config.addresses-url}")
public interface AddressClient {

    @GetMapping("/search")
    public ResponseEntity<List<AddressDTO>> search(@RequestParam(required = false) String street,
                                                   @RequestParam(required = false) String district,
                                                   @RequestParam(required = false) String city);
}
