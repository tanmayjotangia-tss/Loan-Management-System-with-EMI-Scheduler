package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.Loan;
import com.loanmanagementsystem.app.entity.enums.LoanStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByBorrowerId(Long borrowerId);

    List<Loan> findAllByLoanType(LoanType loanType);

    List<Loan> findAllByStatus(LoanStatus status);

    List<Loan> findAllByBorrowerIdAndStatus(Long borrowerId, LoanStatus status);

    Optional<Loan> findByLoanApplicationId(Long loanApplicationId);

    @Query("""
       SELECT COUNT(loan)
       FROM Loan loan
       WHERE loan.borrower.id = :userId
       AND loan.status = 'ACTIVE'
       """)
    Long findNumberOfActiveLoansByBorrowerId(Long userId);
}