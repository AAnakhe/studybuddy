package org.flexisaf.studbud.signup.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank(message = "Full name is required") String fullName,

        @Schema(description = "Phone number", example = "id3velope@studbud.com")
        @NotBlank(message = "Email is required") @Email String email,

        @Schema(description = "Phone number", example = "08012345678")
        @NotBlank(message = "Phone number is required") String phone,

        @NotBlank(message = "Password is required") String password

) {}
