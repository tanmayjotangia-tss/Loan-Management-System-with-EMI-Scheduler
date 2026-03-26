package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanPropertiesRepository extends JpaRepository<LoanProperties, Long> {

    Optional<LoanProperties> findByLoanType(LoanType loanType);

}