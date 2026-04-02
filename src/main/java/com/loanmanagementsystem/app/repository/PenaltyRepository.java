package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    List<Penalty> findAllByLoanId(Long loanId);

    List<Penalty> findAllByLoanIdAndIsPaidFalse(Long loanId);

    Optional<Penalty> findByEmiId(Long emiId);

    List<Penalty> findAllByEmiId(Long emiId);
}