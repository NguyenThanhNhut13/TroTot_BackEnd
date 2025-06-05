/*
 * @ (#) FeignErrorDecoder.java       1.0     16/05/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 16/05/2025
 * @version:    1.0
 */

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import vn.edu.iuh.fit.roomservice.exception.TooManyRequestsException;

public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            return new TooManyRequestsException("Too many requests from Feign client");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
