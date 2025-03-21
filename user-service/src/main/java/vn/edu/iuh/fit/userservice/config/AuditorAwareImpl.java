/*
 * @ (#) AuditorAwareImpl.java       1.0     21/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.config;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 21/03/2025
 * @version:    1.0
 */

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final HttpServletRequest request;

    @Override
    @Nonnull
    public Optional<String> getCurrentAuditor() {
        String userId = request.getHeader("X-User-Id");
        return Optional.ofNullable(userId).filter(id -> !id.isEmpty()).or(() -> Optional.of("SYSTEM"));
    }
}