/*
 * @ (#) CredentialUpdateRequest.java       1.0     11/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.model.dto.request;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 11/04/2025
 * @version:    1.0
 */

import lombok.Data;

@Data
public class CredentialUpdateRequest {
    private String type; // "email" or "phone"
    private String value;
}
