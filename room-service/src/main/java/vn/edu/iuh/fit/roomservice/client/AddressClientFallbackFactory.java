/*
 * @ (#) AddressClientFallbackFactory.java       1.0     13/05/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 13/05/2025
 * @version:    1.0
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.roomservice.model.dto.AddressDTO;
import vn.edu.iuh.fit.roomservice.model.dto.AddressSummaryDTO;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;

import java.util.Collections;
import java.util.List;

@Component
public class AddressClientFallbackFactory implements FallbackFactory<AddressClient> {
    private static final Logger log = LoggerFactory.getLogger(AddressClientFallbackFactory.class);

    @Override
    public AddressClient create(Throwable cause) {
        return new AddressClient() {
            @Override
            public ResponseEntity<BaseResponse<List<AddressDTO>>> searchAddresses(String street, String district, String province) {
                log.error("Fallback triggered for searchAddresses: {}", cause.getMessage());
                return ResponseEntity.ok(new BaseResponse<>(false, "Service unavailable", Collections.emptyList()));
            }

            @Override
            public ResponseEntity<BaseResponse<AddressDTO>> addAddress(AddressDTO address) {
                log.error("Fallback triggered for addAddress: {}", cause.getMessage());
                return ResponseEntity.ok(new BaseResponse<>(false, "Service unavailable", null));
            }

            @Override
            public ResponseEntity<BaseResponse<AddressDTO>> getAddressById(Long id) {
                log.error("Fallback triggered for getAddressById: {}", cause.getMessage());
                return ResponseEntity.ok(new BaseResponse<>(false, "Service unavailable", null));
            }

            @Override
            public ResponseEntity<BaseResponse<AddressDTO>> updateAddress(Long id, AddressDTO newAddress) {
                    log.error("Fallback triggered for updateAddress: {}", cause.getMessage());
                return ResponseEntity.ok(new BaseResponse<>(false, "Service unavailable", null));
            }

            @Override
            public ResponseEntity<BaseResponse<List<AddressDTO>>> getAddressesByIds(List<Long> ids) {
                log.error("Fallback triggered for getAddressesByIds: {}", cause.getMessage());
                return ResponseEntity.ok(new BaseResponse<>(false, "Service unavailable", Collections.emptyList()));
            }

            @Override
            public ResponseEntity<BaseResponse<List<AddressSummaryDTO>>> getAddressSummary(List<Long> ids) {
                log.error("Fallback triggered for getAddressSummary: {}", cause.getMessage());
                return ResponseEntity.ok(new BaseResponse<>(false, "Service unavailable", Collections.emptyList()));
            }

            @Override
            public ResponseEntity<BaseResponse<String>> testRetry() {
                log.error("Fallback triggered for testRetry: {}", cause.getMessage());
                return ResponseEntity.ok(new BaseResponse<>(false, "Service unavailable", null));
            }
        };
    }
}
