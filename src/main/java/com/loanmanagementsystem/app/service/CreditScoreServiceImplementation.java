package com.loanmanagementsystem.app.service;


import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class CreditScoreServiceImplementation implements CreditScoreService {

    private static final int MIN_SCORE = 300;
    private static final int MAX_SCORE = 900;
    private static final int BASE_SCORE = 650;

    @Override
    public int initializer(int currentScore) {
        if (currentScore == -1) {
            return BASE_SCORE;
        }
        return currentScore;
    }

    @Override
    public int updateOnPayment(int currentScore, Emi emi) {

        currentScore = initializer(currentScore);

        LocalDate dueDate = emi.getDueDate();
        LocalDate paymentDate = emi.getPaymentDate();

        long delayDays = paymentDate.toEpochDay() - dueDate.toEpochDay();

        int change;

        if (delayDays < 0) {
            change = rewardCalculator(currentScore) + 2;
        } else if (delayDays == 0) {
            change = rewardCalculator(currentScore);
        } else {
            change = penaltyCalculator(delayDays);
        }

        return limitTheVal(currentScore + change);
    }

    @Override
    public int updateOnOverdue(int currentScore) {

        currentScore = initializer(currentScore);

        int change;

        if (currentScore >= 750) {
            change = -5;
        } else if (currentScore >= 600) {
            change = -8;
        } else {
            change = -10;
        }

        return limitTheVal(currentScore + change);
    }

    @Override
    public int updateOnMissedEmi(int currentScore) {

        currentScore = initializer(currentScore);

        int change;

        if (currentScore >= 750) {
            change = -40;
        } else if (currentScore >= 600) {
            change = -50;
        } else {
            change = -60;
        }

        return limitTheVal(currentScore + change);
    }

    @Override
    public int updateOnForeclosure(int currentScore, int overdueEmiCount, BigDecimal loanAmount) {

        currentScore = initializer(currentScore);

        int baseChange;

        if (overdueEmiCount == 0) {
            baseChange = 10;
        } else if (overdueEmiCount <= 2) {
            baseChange = 5;
        } else if (overdueEmiCount <= 4) {
            baseChange = 0;
        } else {
            baseChange = -10;
        }

        double weightFactor = calculateWeightFactor(loanAmount);
        int finalChange = (int) Math.round(baseChange * weightFactor);
        return limitTheVal(currentScore + finalChange);
    }

    public int updateOnLoanCreation(int currentScore, BigDecimal loanAmount) {
        currentScore = initializer(currentScore);

        int change;

        double loan = loanAmount.doubleValue();

        if (loan < 50000) change = -2;
        else if (loan < 200000) change = -5;
        else if (loan < 500000) change = -8;
        else change = -12;

        return limitTheVal(currentScore + change);
    }


    private int rewardCalculator(int currentScore) {
        if (currentScore < 600) return 8;
        if (currentScore < 700) return 5;
        if (currentScore < 800) return 3;
        return 1;
    }

    private int penaltyCalculator(long delayDays) {
        if (delayDays <= 30) return -10;
        if (delayDays <= 60) return -25;
        return -50;
    }

    private int limitTheVal(int score) {
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }

    private double calculateWeightFactor(BigDecimal amount) {

        double loan = amount.doubleValue();

        if (loan < 50000) return 0.5;
        if (loan < 200000) return 1.0;
        if (loan < 500000) return 1.5;
        return 2;
    }
}