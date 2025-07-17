package org.flexisaf.studbud;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.flexisaf.studbud.auth.config.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@OpenAPIDefinition(info = @Info(contact = @Contact(name = "studbud engineering", email = "studbud@flexisaf.com.ng"), title = "studbud core api.", version = "1.0"))
@SecurityScheme(name = "bearerAuth", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
@SpringBootApplication
@EnableConfigurationProperties(value = {ConfigProperties.class})
public class StudbudApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudbudApplication.class, args);
	}

}
