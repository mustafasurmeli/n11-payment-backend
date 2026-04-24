package com.n11.payment.controller;


import com.n11.payment.dto.PaymentRequest;
import com.n11.payment.dto.PaymentResponse;
import com.n11.payment.entity.PaymentTransaction;
import com.n11.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request, @AuthenticationPrincipal UserDetails userDetails){
        return new PaymentResponse(
                paymentService.pay(request.getMethod(), request.getAmount(), userDetails.getUsername())
        );
    }

    @GetMapping("/me")
    public List<PaymentResponse> getMine(@AuthenticationPrincipal UserDetails userDetails){
        return paymentService.getTransactionsForUser(userDetails.getUsername())
                .stream()
                .map(PaymentResponse::new)
                .toList();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponse> getAll() {
        return paymentService.getAllTransactions()
                .stream()
                .map(PaymentResponse::new)
                .toList();
    }
}
