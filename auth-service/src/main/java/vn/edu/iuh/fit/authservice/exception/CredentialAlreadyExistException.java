/*
 * @ (#) CredentialAlreadyExistException.java       1.0     11/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.exception;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 11/04/2025
 * @version:    1.0
 */

public class CredentialAlreadyExistException extends RuntimeException {
    public CredentialAlreadyExistException(String message) {
        super(message);
    }
}
