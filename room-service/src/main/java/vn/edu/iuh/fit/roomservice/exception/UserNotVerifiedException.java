/*
 * @ (#) UserNotVerifiedException.java       1.0     29/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.exception;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 29/03/2025
 * @version:    1.0
 */

public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException(String message) {
        super(message);
    }
}
