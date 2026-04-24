package com.n11.payment.auth;


import com.n11.payment.auth.dto.*;
import com.n11.payment.auth.refresh.RefreshToken;
import com.n11.payment.auth.refresh.RefreshTokenService;
import com.n11.payment.exception.AuthException;
import com.n11.payment.exception.ConflictException;
import com.n11.payment.security.JwtService;
import com.n11.payment.user.Role;
import com.n11.payment.user.User;
import com.n11.payment.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse register(RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername()))
            throw new ConflictException("Username already taken");
        if(userRepository.existsByEmail(request.getEmail()))
            throw new ConflictException("Email already registered");

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                Role.USER
        );

        User saved = userRepository.save(user);

        return  new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole()
        );
    }

    public TokenResponse login(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new AuthException("Invalid username or password");

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createFor(user);

        return new TokenResponse(accessToken, refreshToken.getToken());
    }

    public TokenResponse refresh(RefreshRequest request){
        RefreshToken stored = refreshTokenService.verifyAndGet(request.getRefreshToken());
        User user = stored.getUser();

        refreshTokenService.revoke(stored);

        String newAccessToken = jwtService.generateAccessToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createFor(user);

        return new TokenResponse(newAccessToken, newRefreshToken.getToken());
    }

    public void logout(RefreshRequest request){
        RefreshToken stored = refreshTokenService.verifyAndGet(request.getRefreshToken());
        refreshTokenService.revoke(stored);

    }
}
