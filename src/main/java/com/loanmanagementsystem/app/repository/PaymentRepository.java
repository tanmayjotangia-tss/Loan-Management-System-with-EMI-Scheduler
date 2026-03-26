package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByLoanId(Long loanId);

    List<Payment> findAllByEmiId(Long emiId);

    Optional<Payment> findByEmiId(Long emiId);
}