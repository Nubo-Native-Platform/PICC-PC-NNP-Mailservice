package com.nubons.nnpmailservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger documentation configuration for NNP Mail Service.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NNP Mail Service API")
                        .version("0.0.1")
                        .description("REST API microservice for queuing, sending (via JMS and SMTP), and tracking emails with template support and attachments for the Nubons NNP Platform.")
                        .contact(new Contact()
                                .name("Nubons Platform Team")
                                .email("support@nubons.com")
                                .url("https://nubons.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("/").description("Default Server (Relative)"),
                        new Server().url("http://localhost:" + serverPort).description("Local Development Server")
                ));
    }
}
