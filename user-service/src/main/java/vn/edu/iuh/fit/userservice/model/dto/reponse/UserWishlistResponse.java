/*
 * @ (#) UserWishlistResponse.java       1.0     24/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.model.dto.reponse;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 24/04/2025
 * @version:    1.0
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWishlistResponse {
    private Long userId;
    private List<Long> roomIds;
}
