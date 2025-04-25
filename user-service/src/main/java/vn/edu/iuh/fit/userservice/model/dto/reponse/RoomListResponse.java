/*
 * @ (#) RoomListResponse.java       1.0     22/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.model.dto.reponse;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 22/04/2025
 * @version:    1.0
 */

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomListResponse {
    private Long id;
    private String title;
    private double price;
    private double area;
    private String roomType;
    private List<String> imageUrls;
    private String district;
    private String province;
}
