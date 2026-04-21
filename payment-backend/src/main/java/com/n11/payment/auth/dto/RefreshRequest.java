package com.n11.payment.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest {
    @NotBlank(message = "Refressh token is required")
    private String refreshToken;

    public String getRefreshToken(){return refreshToken;}
    public void setRefreshToken(String refreshToken){ this.refreshToken=refreshToken;}
}
