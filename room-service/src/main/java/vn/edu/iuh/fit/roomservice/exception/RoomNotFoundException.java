/*
 * @ (#) UserNotFoundException.java       1.0     21/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.exception;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 21/03/2025
 * @version:    1.0
 */

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(String message) {
        super(message);
    }
}