/*
 * @ (#) RefreshAccessTokenResponse.java       1.0     08/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.model.dto.response;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 08/04/2025
 * @version:    1.0
 */

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
