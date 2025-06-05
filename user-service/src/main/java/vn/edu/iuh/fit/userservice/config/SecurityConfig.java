/*
 * @ (#) SecurityConfig.java       1.0     26/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.config;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 26/03/2025
 * @version:    1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.edu.iuh.fit.userservice.filter.JwtAuthTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthTokenFilter jwtAuthTokenFilter;

    public SecurityConfig(JwtAuthTokenFilter jwtAuthTokenFilter) {
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/profile").hasAnyAuthority("ADMIN", "USER", "LANDLORD")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users").hasAnyAuthority("ADMIN", "USER", "LANDLORD")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/add-posts").hasAnyAuthority("ADMIN", "LANDLORD")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/use-post-slot").hasAnyAuthority("ADMIN", "LANDLORD")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/wish-list/{roomId}").hasAnyAuthority("ADMIN", "USER", "LANDLORD")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/wish-list").hasAnyAuthority("ADMIN", "USER", "LANDLORD")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/wish-list/{roomId}").hasAnyAuthority("ADMIN", "USER", "LANDLORD")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/create").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/wish-list/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}/wish-list").permitAll()

                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-sources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
