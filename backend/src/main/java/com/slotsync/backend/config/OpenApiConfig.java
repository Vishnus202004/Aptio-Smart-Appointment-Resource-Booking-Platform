package com.slotsync.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 / Swagger UI configuration.
 *
 * <p>Registers a global Bearer token security scheme so that the Swagger UI
 * "Authorize" button injects the JWT into all protected endpoint calls.
 * Accessible at: {@code /swagger-ui.html}
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI slotSyncOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, bearerAuthScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("SlotSync API")
                .description("""
                        ## SlotSync — Smart Appointment & Resource Booking Platform
                        
                        A production-grade booking system that guarantees **zero double bookings**
                        through transactional pessimistic locking and FIFO waitlist management.
                        
                        ### Authentication
                        1. Register via `POST /api/v1/auth/register`
                        2. Login via `POST /api/v1/auth/login` to receive `accessToken` and `refreshToken`
                        3. Click the **Authorize** button above and paste the `accessToken`
                        4. Use `POST /api/v1/auth/refresh` to rotate tokens before expiry
                        
                        ### Roles
                        - **ADMIN** — Full platform management
                        - **PROVIDER** — Manages slots, schedules, and views analytics
                        - **CUSTOMER** — Searches providers, books slots, manages bookings
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("SlotSync Engineering")
                        .email("dev@slotsync.io")
                        .url("https://slotsync.io"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> serverList() {
        return List.of(
                new Server().url("http://localhost:" + serverPort).description("Local Development"),
                new Server().url("https://api.slotsync.io").description("Production")
        );
    }

    private SecurityScheme bearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter the JWT access token obtained from /api/v1/auth/login");
    }
}
