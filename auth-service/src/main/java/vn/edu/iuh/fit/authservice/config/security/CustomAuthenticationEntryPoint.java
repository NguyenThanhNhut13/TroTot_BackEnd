/*
 * @ (#) CustomAuthenticationEntryPoint.java       1.0     10/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.config.security;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/04/2025
 * @version:    1.0
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        Map.of(
                                "status", 401,
                                "error", "Unauthorized",
                                "message", "You need to login to access this resource.",
                                "path", request.getRequestURI()
                        )
                )
        );
    }
}
