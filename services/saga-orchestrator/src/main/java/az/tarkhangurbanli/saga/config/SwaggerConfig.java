package az.tarkhangurbanli.saga.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Saga Orchestration Service REST API",
                version = "1.0",
                description = "Comprehensive documentation for the Saga Orchestration Service REST API," +
                        " including endpoint descriptions and usage instructions.",
                contact = @Contact(
                        name = "Tarkhan Gurbanli",
                        email = "tarkhangurbanli@gmail.com"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Access the full API documentation",
                url = "http://localhost:8080/swagger-ui/index.html"
        )
)
public class SwaggerConfig {
}
