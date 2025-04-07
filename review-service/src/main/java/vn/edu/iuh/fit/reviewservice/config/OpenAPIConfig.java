/*
 * @ (#) OpenAPIConfig.java       1.0     07/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.reviewservice.config;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 07/04/2025
 * @version:    1.0
 */

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Review Service API")
                        .version("1.0")
                        .description("API docs for Review service")
                        .contact(new Contact().name("Nguyễn Thanh Nhứt").email("thanhnhutcu@gmail.com")));
    }
}
