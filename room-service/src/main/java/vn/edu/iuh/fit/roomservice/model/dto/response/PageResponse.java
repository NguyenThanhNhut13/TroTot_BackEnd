/*
 * @ (#) PageResponse.java       1.0     15/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.model.dto.response;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 15/04/2025
 * @version:    1.0
 */

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}

