package com.max.citas.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Citas API")
                        .description("API para gestionar citas médicas")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Server local"),
                        new Server()
                                .url("http://localhost:8090/api/citas")
                                .description("Server API Gateway")
                ));
    }
}
