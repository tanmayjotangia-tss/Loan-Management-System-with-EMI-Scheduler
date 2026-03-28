package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.repository.LoanPropertiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanPropertiesServiceImplementation implements LoanPropertiesService {

    private final LoanPropertiesRepository loanPropertiesRepository;

    @Override
    public LoanProperties updateLoanProperties(LoanType loanType, LoanProperties updatedProperties) {
        LoanProperties loanProperties = loanPropertiesRepository.findByLoanType(loanType)
                .orElseThrow(() -> new RuntimeException("Loan properties not found for type: " + loanType));

        if (updatedProperties.getInterestRate() != null) {
            loanProperties.setInterestRate(updatedProperties.getInterestRate());
        }
        if (updatedProperties.getMinAmount() != null) {
            loanProperties.setMinAmount(updatedProperties.getMinAmount());
        }
        if (updatedProperties.getMaxAmount() != null) {
            loanProperties.setMaxAmount(updatedProperties.getMaxAmount());
        }
        if (updatedProperties.getMinTenure() != null) {
            loanProperties.setMinTenure(updatedProperties.getMinTenure());
        }
        if (updatedProperties.getMaxTenure() != null) {
            loanProperties.setMaxTenure(updatedProperties.getMaxTenure());
        }
        if (updatedProperties.getLatePaymentPenaltyPercentage() != null) {
            loanProperties.setLatePaymentPenaltyPercentage(updatedProperties.getLatePaymentPenaltyPercentage());
        }
        if (updatedProperties.getMissedEmiPenaltyPercentage() != null) {
            loanProperties.setMissedEmiPenaltyPercentage(updatedProperties.getMissedEmiPenaltyPercentage());
        }
        if (updatedProperties.getGracePeriodDays() != null) {
            loanProperties.setGracePeriodDays(updatedProperties.getGracePeriodDays());
        }
        if (updatedProperties.getMinRequiredCibilScore() != null) {
            loanProperties.setMinRequiredCibilScore(updatedProperties.getMinRequiredCibilScore());
        }

        return loanPropertiesRepository.save(loanProperties);
    }

    @Override
    public LoanProperties displayLoanProperties(LoanType loanType) {
        return loanPropertiesRepository.findByLoanType(loanType)
                .orElseThrow(() -> new RuntimeException("Loan properties not found for type: " + loanType));
    }
}
