package org.flexisaf.studbud.signup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.flexisaf.studbud.model.AppUser;
import org.flexisaf.studbud.repository.AppUserRepository;
import org.flexisaf.studbud.repository.EmailVerificationCodeRepository;
import org.flexisaf.studbud.util.ApiErrorResponse;
import org.flexisaf.studbud.util.EmailVerificationCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Signup", description = "Endpoints for user registration")
public class VerificationController {

    private final AppUserRepository userRepository;
    private final EmailVerificationCodeRepository codeRepository;


    @Operation(summary = "Verify OTP code sent to email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Verification code has expired",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid code",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        EmailVerificationCode verificationCode = codeRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid code"));

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Verification code has expired.");
        }

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        codeRepository.delete(verificationCode);

        return ResponseEntity.ok("Email verified successfully.");
    }
}

