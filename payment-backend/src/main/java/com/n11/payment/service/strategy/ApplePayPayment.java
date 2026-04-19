package com.n11.payment.service.strategy;


import org.springframework.stereotype.Service;

@Service
public class ApplePayPayment implements IPayment{

    @Override
    public String getName() {
        return "apple-pay";
    }

    @Override
    public boolean pay(double amount) {
        System.out.println(amount + " paid with Apple Pay");
        return true;
    }
}
