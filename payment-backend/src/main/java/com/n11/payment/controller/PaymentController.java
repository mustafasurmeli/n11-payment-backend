package com.n11.payment.controller;


import com.n11.payment.dto.PaymentRequest;
import com.n11.payment.dto.PaymentResponse;
import com.n11.payment.entity.PaymentTransaction;
import com.n11.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @GetMapping("/methods")
    public List<String> getAvailableMethods(){
        return paymentService.getAvailableMethods();
    }

    @PostMapping
    public PaymentTransaction pay(@Valid @RequestBody PaymentRequest request){
        return paymentService.pay(request.getMethod(), request.getAmount());
    }

    @GetMapping
    public List<PaymentTransaction> getAll() {
        return paymentService.getAllTransactions();
    }
}
