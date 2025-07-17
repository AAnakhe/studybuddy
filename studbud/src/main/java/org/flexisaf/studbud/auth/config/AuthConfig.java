package org.flexisaf.studbud.auth.config;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flexisaf.studbud.model.AppUser;
import org.flexisaf.studbud.repository.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
@DependsOn("entityManagerFactory")
public class AuthConfig {
    private final AppUserRepository appUserRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(() -> {
                log.error("Email {} was not found in database", email);
                return new EntityNotFoundException(email + " Invalid email or password.");
            });

            return User.builder()
                    .username(appUser.getEmail())
                    .password(appUser.getPassword())
                    .authorities(new SimpleGrantedAuthority(appUser.getRole().name()))
                    .accountLocked(false) // You can make this dynamic if needed
                    .disabled(!appUser.isEnabled())
                    .build();
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


}
