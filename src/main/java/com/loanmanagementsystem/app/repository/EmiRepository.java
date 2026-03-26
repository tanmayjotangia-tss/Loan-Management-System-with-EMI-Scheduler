package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmiRepository extends JpaRepository<Emi, Long> {

    List<Emi> findAllByLoanIdOrderByInstallmentNumberAsc(Long loanId);

    List<Emi> findAllByStatus(EmiStatus status);

    List<Emi> findAllByLoanIdAndStatus(Long loanId, EmiStatus status);

    List<Emi> findAllByDueDateBeforeAndStatus(LocalDate date, EmiStatus status);

    Optional<Emi> findByLoanIdAndInstallmentNumber(Long loanId, Integer installmentNumber);
}