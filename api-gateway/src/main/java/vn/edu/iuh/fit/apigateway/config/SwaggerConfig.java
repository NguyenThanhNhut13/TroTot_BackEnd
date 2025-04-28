/*
 * @ (#) SwaggerConfig.java       1.0     07/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.apigateway.config;
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
public class SwaggerConfig {

    @Bean
    public OpenAPI apiGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gateway")
                        .description("Microservices API Documentation")
                        .version("1.0.0")
                        .contact(new Contact().name("Nguyễn Thanh Nhứt").email("thanhnhutcu@gmail.com"))
                );
    }
}
