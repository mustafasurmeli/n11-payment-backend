package com.n11.payment.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_tranctions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Boolean success;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PaymentTransaction() {}

    public PaymentTransaction(String method, Double amount, Boolean success){
        this.method = method;
        this.amount = amount;
        this.success = success;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getMethod() { return method;}
    public Double getAmount() { return amount;}
    public Boolean getSuccess() { return  success;}
    public LocalDateTime getCreatedAt() {return createdAt;}

}
