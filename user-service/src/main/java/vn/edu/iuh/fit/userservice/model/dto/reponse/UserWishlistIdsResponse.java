/*
 * @ (#) UserWishlistIdsResponse.java       1.0     12/05/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.model.dto.reponse;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 12/05/2025
 * @version:    1.0
 */

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWishlistIdsResponse {
    private List<Long> roomIds;
}
