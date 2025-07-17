package org.flexisaf.studbud.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flexisaf.studbud.auth.jwt.JwtTokenProvider;
import org.flexisaf.studbud.auth.data.LoginRequest;
import org.flexisaf.studbud.model.AppUser;
import org.flexisaf.studbud.repository.AppUserRepository;
import org.flexisaf.studbud.util.ApiErrorResponse;
import org.flexisaf.studbud.util.ApiResponse;
import org.flexisaf.studbud.util.ErrorType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class AuthService extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AppUserRepository appUserRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
            var authToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            log.error("Invalid login request format: {}", e.getMessage(), e);
            throw new AuthenticationServiceException("Invalid request payload");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException {
        AppUser user = appUserRepository.findByEmail(authResult.getName())
                .orElseThrow(() -> new UsernameNotFoundException("This email does not exist."));

        var token = tokenProvider.accessToken(authResult);
        var result = new ApiResponse(true, Map.of(
                "access_token", token,
                "user", user
        ), "Login successful");

        writeJsonResponse(response, HttpServletResponse.SC_OK, result);
        SecurityContextHolder.getContext().setAuthentication(authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        String errorMessage = switch (failed.getClass().getSimpleName()) {
            case "BadCredentialsException" -> "Invalid email or password.";
            case "UsernameNotFoundException" -> "User not found.";
            default -> "Authentication failed.";
        };

        var errorResponse = new ApiErrorResponse(false, null, errorMessage, ErrorType.ERROR);
        writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, errorResponse);
    }

    private void writeJsonResponse(HttpServletResponse response, int status, Object body) {
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(body));
        } catch (IOException e) {
            log.error("Error writing JSON response: {}", e.getMessage(), e);
        }
    }
}
