/*
 * @ (#) ValidationException.java       1.0     20/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.exception;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 20/04/2025
 * @version:    1.0
 */

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
