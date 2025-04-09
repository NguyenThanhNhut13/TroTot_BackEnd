/*
 * @ (#) BadRequestException.java       1.0     08/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.mediaservice.exception;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 08/04/2025
 * @version:    1.0
 */

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
