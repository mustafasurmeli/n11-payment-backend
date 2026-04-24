package com.n11.payment.auth;


import com.n11.payment.auth.dto.LoginRequest;
import com.n11.payment.auth.dto.RegisterRequest;
import com.n11.payment.auth.dto.TokenResponse;
import com.n11.payment.auth.dto.UserResponse;
import com.n11.payment.auth.refresh.RefreshToken;
import com.n11.payment.auth.refresh.RefreshTokenService;
import com.n11.payment.exception.AuthException;
import com.n11.payment.exception.ConflictException;
import com.n11.payment.security.JwtService;
import com.n11.payment.user.Role;
import com.n11.payment.user.User;
import com.n11.payment.user.UserRepository;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldSucceedWhenUsernameAndEmailAreAvailable() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("musti");
        request.setEmail("musti@test.com");
        request.setPassword("abc123");

        when(userRepository.existsByUsername("musti")).thenReturn(false);
        when(userRepository.existsByEmail("musti@test.com")).thenReturn(false);
        when(passwordEncoder.encode("abc123")).thenReturn("hashedPassword");

        User savedUser = new User("musti", "musti@test.com", " hashedPassword", Role.USER);
        setUserId(savedUser, 1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse result = authService.register(request);

        assertThat(result.getUsername()).isEqualTo("musti");
        assertThat(result.getEmail()).isEqualTo("musti@test.com");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        assertThat(result.getId()).isEqualTo(1L);
    }

    private void setUserId(User user, Long id){
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void registerShouldThrowConflictWhenUsernameAlreadyTaken(){

        RegisterRequest request = new RegisterRequest();
        request.setUsername("musti");
        request.setEmail("musti@test.com");
        request.setPassword("abc123");

        when(userRepository.existsByUsername("musti")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerShouldThrowConglictWhenEmailAlreadyRegistered(){

        RegisterRequest request = new RegisterRequest();
        request.setUsername("musti");
        request.setEmail("musti@test.com");
        request.setPassword("abc123");

        when(userRepository.existsByUsername("musti")).thenReturn(false);
        when(userRepository.existsByEmail("musti@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldReturnTokensWhenCredentialsAreCorrect() {

        LoginRequest request = new LoginRequest();
        request.setUsername("musti");
        request.setPassword("abc123");

        User user = new User("musti", "musti@test.com", "hashedPassword", Role.USER);
        setUserId(user, 1L);

        when(userRepository.findByUsername("musti")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("abc123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("fake-access-token");

        RefreshToken refreshToken = new RefreshToken("fake-refresh-token", user, Instant.now().plusSeconds(3600));
        when(refreshTokenService.createFor(user)).thenReturn(refreshToken);

        TokenResponse result = authService.login(request);

        assertThat(result.getAccessToken()).isEqualTo("fake-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("fake-refresh-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void loginShouldThrowAuthExceptionWhenUserNotFound(){

        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("abc123");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Invalid username or password");

        verify(jwtService, never()).generateAccessToken(any());
        verify(refreshTokenService, never()).createFor(any());
    }

    @Test
    void loginShouldThrowAuthExceptionWhenPasswordIsWrong(){

        LoginRequest request = new LoginRequest();
        request.setUsername("musti");
        request.setPassword("wrong_password");

        User user = new User("musti", "musti@test.com", "hashedPassword", Role.USER);
        setUserId(user, 1L);

        when(userRepository.findByUsername("musti")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Invalid username or password");

        verify(jwtService, never()).generateAccessToken(any());
        verify(refreshTokenService, never()).createFor(any());
    }
}
