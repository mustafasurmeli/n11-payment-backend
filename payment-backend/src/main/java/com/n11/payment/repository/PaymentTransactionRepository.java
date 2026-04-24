package com.n11.payment.repository;

import com.n11.payment.entity.PaymentTransaction;
import com.n11.payment.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByUserOrderByCreatedAtDesc(User user);
}
