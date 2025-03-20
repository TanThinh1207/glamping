package com.group2.glamping.configuration;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Avocado",
                        email = "huatanthinh1207@gmail.com"
                ),
                description = "OpenApi documentation for Glamping application",
                title = "OpenApi specification - Glamping",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local server",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Deploy server",
                        url = "http://18.140.180.54:8080"
                )
        }
)
public class OpenApiConfig {
}
