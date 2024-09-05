package com.stackoverflow.beta.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("stackoverflow")
                .pathsToMatch("/**") // Adjust this to match your API paths
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StackOverFlow Service")
                        .version("1.0.0")
                        .description("API Documentation")
                        .contact(new Contact()
                                .name("Stack Overflow")
                                .url("yourwebsite.com")
                                .email("your-email@example.com"))
                );
    }
}
