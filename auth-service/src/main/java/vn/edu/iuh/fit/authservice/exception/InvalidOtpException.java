/*
 * @ (#) InvalidOtpException.java       1.0     18/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.exception;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 18/03/2025
 * @version:    1.0
 */

public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
