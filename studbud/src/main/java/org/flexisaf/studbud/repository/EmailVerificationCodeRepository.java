package org.flexisaf.studbud.repository;

import org.flexisaf.studbud.util.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {
    Optional<EmailVerificationCode> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
}

