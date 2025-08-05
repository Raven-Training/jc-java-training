package com.raven.training.config.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.HttpHeaders;

/**
 * Configuration class for OpenAPI 3.0, used by Swagger UI.
 * This class defines global information about the API, such as its title,
 * description, version, and contact details. It also configures the available
 * servers and the security scheme for authentication using a JWT token.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0.0
 * @since 2025-08-05
 */
@OpenAPIDefinition(
        info = @Info(
                title = "TRAINING APP",
                description = "Application for books",
                version = "1.0.0",
                contact = @Contact(
                        name = "Juan Esteban Camacho Barrera",
                        email = "jecb1913@gmail.com"
                )
        ),
        servers ={
                @Server(
                        url = "http://localhost:8081",
                        description = "DEV SERVER"
                ),
                @Server(
                        url = "http://books:8081",
                        description = "PROD SERVER"
                )
        },
        security = @SecurityRequirement(
                name = "Security token"
        ))
@SecurityScheme(
        name = "Security token",
        description = "Access token for my Api",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}
