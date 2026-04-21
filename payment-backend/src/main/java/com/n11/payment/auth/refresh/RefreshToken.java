package com.n11.payment.auth.refresh;


import com.n11.payment.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false)
    private Instant createdAt;

    public RefreshToken() {}

    public RefreshToken(String token, User user, Instant expiresAt){
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
        this.revoked =false;
    }

    public Long getId() {return id;}
    public String getToken() { return token;}
    public User getUser() {return user;}
    public Instant getExpiresAt() { return expiresAt;}
    public boolean isRevoked() {return revoked;}
    public void setRevoked(boolean revoked) {this.revoked = revoked;}
    public Instant getCreatedAt() {return createdAt;}

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive(){
        return !revoked && !isExpired();
    }

}
