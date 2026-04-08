package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CreditScoreService {
    int initializer(int currentScore);

    int updateOnPayment(int currentScore, Emi emi);

    int updateOnOverdue(int currentScore);

    int updateOnMissedEmi(int currentScore);

    int updateOnForeclosure(int currentScore, int overdueEmiCount, BigDecimal loanAmount);

    int updateOnLoanCreation(int currentScore, BigDecimal loanAmount);
}