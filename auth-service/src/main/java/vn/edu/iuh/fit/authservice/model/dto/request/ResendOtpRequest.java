/*
 * @ (#) ResendOtpRequest.java       1.0     17/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.model.dto.request;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 17/04/2025
 * @version:    1.0
 */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.authservice.enumerate.OtpPurpose;

@Getter
@Setter
public class ResendOtpRequest {
    @NotBlank
    private String credential;

    @NotNull
    private OtpPurpose purpose;
}
