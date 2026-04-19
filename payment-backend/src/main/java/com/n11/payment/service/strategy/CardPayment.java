package com.n11.payment.service.strategy;


import org.springframework.stereotype.Service;

@Service
public class CardPayment implements IPayment{

    @Override
    public String getName() {
        return "credit-card";
    }

    @Override
    public boolean pay(double amount) {
        System.out.println(amount + " paid with credit card");
        return Math.random() >0.5;
    }
}
