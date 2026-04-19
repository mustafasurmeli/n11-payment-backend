package com.n11.payment.service.strategy;

public interface IPayment {
    String getName();
    boolean pay(double amount);
}
