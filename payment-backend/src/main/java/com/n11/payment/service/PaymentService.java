package com.n11.payment.service;


import com.n11.payment.auth.dto.UserResponse;
import com.n11.payment.entity.PaymentTransaction;
import com.n11.payment.exception.ResourceNotFoundException;
import com.n11.payment.repository.PaymentTransactionRepository;
import com.n11.payment.service.notification.NotificationHandler;
import com.n11.payment.service.strategy.IPayment;
import com.n11.payment.user.User;
import com.n11.payment.user.UserRepository;
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
    private final UserRepository userRepository;

    public PaymentService(List<IPayment> paymentList,
                          PaymentTransactionRepository repository,
                          NotificationHandler notificationChain,
                          UserRepository userRepository){
        this.paymentMethods = paymentList.stream()
                .collect(Collectors.toMap(IPayment::getName, Function.identity()));
        this.repository = repository;
        this.notificationChain = notificationChain;
        this.userRepository = userRepository;
    }

    public PaymentTransaction pay(String methodName, double amount, String username){
        IPayment payment = paymentMethods.get(methodName);
        if (payment==null){
            throw new IllegalArgumentException("Unknown payment method: " + methodName);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean success = payment.pay(amount);

        PaymentTransaction transaction = new PaymentTransaction(methodName, amount, success, user);
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

    public List<PaymentTransaction> getTransactionsForUser(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return repository.findByUserOrderByCreatedAtDesc(user);
    }
}
