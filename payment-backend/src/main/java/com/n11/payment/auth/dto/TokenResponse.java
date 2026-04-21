package com.n11.payment.auth.dto;

public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() { return accessToken;}
    public String getTokenType() { return tokenType;}
    public String getRefreshToken() {return refreshToken;}
}
