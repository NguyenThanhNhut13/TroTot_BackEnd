/*
 * @ (#) UnauthorizedException.java       1.0     08/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.exception;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 08/04/2025
 * @version:    1.0
 */

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
