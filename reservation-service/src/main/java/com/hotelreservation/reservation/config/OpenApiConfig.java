package com.hotelreservation.reservation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Reservation Service API", version = "1.0", description = "Hotel reservation management"))
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI();
    }
}
