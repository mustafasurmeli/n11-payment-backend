package com.n11.payment.dto;

import com.n11.payment.entity.PaymentTransaction;

import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private String method;
    private Double amount;
    private Boolean success;
    private LocalDateTime createdAt;
    private String username;

    public PaymentResponse(PaymentTransaction transaction){
        this.id = transaction.getId();
        this.method = transaction.getMethod();
        this.amount = transaction.getAmount();
        this.success = transaction.getSuccess();
        this.createdAt = transaction.getCreatedAt();
        this.username = transaction.getUser().getUsername();
    }

    public Long getId() { return id;}
    public String getMethod() { return method;}
    public Double getAmount() { return amount;}
    public Boolean getSuccess() { return success;}
    public LocalDateTime getCreatedAt() { return createdAt;}
    public String getUsername() { return username;}
}
