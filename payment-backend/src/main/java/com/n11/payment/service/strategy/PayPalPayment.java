package com.n11.payment.service.strategy;


import org.springframework.stereotype.Service;

@Service
public class PayPalPayment implements IPayment{

    @Override
    public String getName() {
        return "paypal";
    }

    @Override
    public boolean pay(double amount) {
        System.out.println(amount+ " paid with PayPal");
        return true;
    }
}
