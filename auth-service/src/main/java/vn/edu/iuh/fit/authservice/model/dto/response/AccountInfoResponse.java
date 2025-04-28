/*
 * @ (#) AccountInfoResponse.java       1.0     11/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.model.dto.response;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 11/04/2025
 * @version:    1.0
 */

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountInfoResponse {
    private Long userId;
    private CredentialStatus email;
    private CredentialStatus phoneNumber;
}
