package com.n11.payment.service;


import com.n11.payment.entity.PaymentTransaction;
import com.n11.payment.repository.PaymentTransactionRepository;
import com.n11.payment.service.notification.NotificationHandler;
import com.n11.payment.service.strategy.IPayment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final Map<String, IPayment> paymentMethods;
    private final PaymentTransactionRepository repository;
    private final NotificationHandler notificationChain;

    public PaymentService(List<IPayment> paymentList,
                          PaymentTransactionRepository repository,
                          NotificationHandler notificationChain){
        this.paymentMethods = paymentList.stream()
                .collect(Collectors.toMap(IPayment::getName, Function.identity()));
        this.repository = repository;
        this.notificationChain = notificationChain;
    }

    public PaymentTransaction pay(String methodName, double amount){
        IPayment payment = paymentMethods.get(methodName);
        if (payment==null){
            throw new IllegalArgumentException("Unknown payment method: " + methodName);
        }

        boolean success = payment.pay(amount);

        PaymentTransaction transaction = new PaymentTransaction(methodName, amount, success);
        PaymentTransaction saved = repository.save(transaction);

        if(!success){
            notificationChain.notify(
                    "Payment failed for " + methodName + ", amount: "+amount
            );
        }
        return saved;
    }

    public List<String> getAvailableMethods() {
        return paymentMethods.keySet().stream().toList();
    }

    public List<PaymentTransaction> getAllTransactions(){
        return repository.findAll();
    }
}
