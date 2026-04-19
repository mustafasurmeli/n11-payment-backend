package com.n11.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class PaymentRequest {

    @NotBlank(message = "Payment method is required")
    private String method;
    @Positive(message = "Amount must be greater than zero")
    private double amount;

    public String getMethod() { return method;}
    public void setMethod(String method) {this.method = method;}
    public double getAmount() { return amount;}
    public void setAmount(double amount) {this.amount = amount;}
}
