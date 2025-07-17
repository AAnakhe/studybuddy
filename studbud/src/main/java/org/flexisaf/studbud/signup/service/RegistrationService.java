package org.flexisaf.studbud.signup.service;

import lombok.RequiredArgsConstructor;
import org.flexisaf.studbud.exception.DuplicateUserException;
import org.flexisaf.studbud.model.AppUser;
import org.flexisaf.studbud.model.data.Role;
import org.flexisaf.studbud.repository.AppUserRepository;
import org.flexisaf.studbud.repository.EmailVerificationCodeRepository;
import org.flexisaf.studbud.signup.data.SignUpRequest;
import org.flexisaf.studbud.util.ApiResponse;
import org.flexisaf.studbud.util.EmailVerificationCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationCodeRepository codeRepository;
    private final EmailService emailService;

    public ApiResponse registerUser(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateUserException("Email already exists");
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.STUDENT);
        user.setEnabled(false);
        userRepository.save(user);

        String code = String.format("%06d", new Random().nextInt(999999));
        codeRepository.deleteByEmail(user.getEmail());

        EmailVerificationCode verificationCode = new EmailVerificationCode();
        verificationCode.setEmail(user.getEmail());
        verificationCode.setCode(code);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        codeRepository.save(verificationCode);

        emailService.sendVerificationEmail(user.getEmail(), code);

        return new ApiResponse(true, null, "Verification code sent to your email.");
    }
}

