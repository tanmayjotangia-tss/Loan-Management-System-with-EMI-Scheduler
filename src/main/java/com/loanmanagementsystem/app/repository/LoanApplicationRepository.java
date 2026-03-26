package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.LoanApplication;
import com.loanmanagementsystem.app.entity.enums.LoanApplicationStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findAllByBorrowerId(Long borrowerId);

    List<LoanApplication> findAllByStatus(LoanApplicationStatus status);

    List<LoanApplication> findAllByStatusAndLoanType(LoanApplicationStatus status, LoanType loanType);

    Optional<LoanApplication> findByIdAndStatus(Long id, LoanApplicationStatus status);

    int countByBorrowerIdAndStatus(Long borrowerId, LoanApplicationStatus status);
}