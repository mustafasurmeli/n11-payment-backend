package com.n11.payment.auth.refresh;


import com.n11.payment.security.JwtService;
import com.n11.payment.user.User;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository repository, JwtService jwtService){
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public RefreshToken createFor(User user){
        String token = jwtService.generateRefreshToken(user);
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshTokenExpirationMs());

        RefreshToken refreshToken = new RefreshToken(token, user, expiresAt);
        return repository.save(refreshToken);
    }

    public RefreshToken verifyAndGet(String tokenString){
        RefreshToken token = repository.findByToken(tokenString)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if(!token.isActive())
            throw new IllegalArgumentException("Refresh token expired or revoked");

        if(!jwtService.isTokenValid(tokenString) || !"refresh".equals(jwtService.extractTokenType(tokenString)))
            throw new IllegalArgumentException("Invalid refresh token");

        return token;
    }

    public void revoke(RefreshToken token){
        token.setRevoked(true);
        repository.save(token);
    }

    public void revokeAllForUser(User user){
        repository.revokeAllByUser(user);
    }
}
