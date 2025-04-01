/*
 * @ (#) UserNotFoundException.java       1.0     01/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.exception;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 01/04/2025
 * @version:    1.0
 */

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
