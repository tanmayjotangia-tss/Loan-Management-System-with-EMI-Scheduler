package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;
import com.loanmanagementsystem.app.entity.LoanApplication;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.*;
import com.loanmanagementsystem.app.exception.BadRequestException;
import com.loanmanagementsystem.app.factory.LoanStrategyFactory;
import com.loanmanagementsystem.app.mapper.LoanMapper;
import com.loanmanagementsystem.app.repository.EmiRepository;
import com.loanmanagementsystem.app.repository.LoanApplicationRepository;
import com.loanmanagementsystem.app.repository.LoanPropertiesRepository;
import com.loanmanagementsystem.app.repository.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImplementation implements LoanService {

    private final LoanRepository loanRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanPropertiesRepository loanPropertiesRepository;
    private final EmiRepository emiRepository;
    private final LoanMapper loanMapper;
    private final LoanStrategyFactory strategyFactory;

    @Transactional
    @Override
    public LoanResponse createLoanFromApplication(Long applicationId, StrategyType type) {
        LoanApplication loanApplication = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadRequestException("Loan application not found with id: " + applicationId));

        if (loanApplication.getStatus() != LoanApplicationStatus.APPROVED) {
            throw new RuntimeException("Loan application must be APPROVED to create a loan. Current status: " + loanApplication.getStatus());
        }

        // Check if a loan already exists for this application
        if (loanRepository.findByLoanApplicationId(applicationId).isPresent()) {
            throw new RuntimeException("A loan already exists for application id: " + applicationId);
        }

        LoanProperties loanProperties = loanPropertiesRepository.findByLoanType(loanApplication.getLoanType())
                .orElseThrow(() -> new RuntimeException("Loan properties not found for type: " + loanApplication.getLoanType()));

        BigDecimal interestRate = loanProperties.getInterestRate();
        BigDecimal principalAmount = loanApplication.getRequestedAmount();
        Integer tenureMonths = loanApplication.getRequestedTenureMonths();

        if(type==null){
            type=loanApplication.getSuggestedStrategy();
        }
        if (type == null) {
            throw new RuntimeException("Strategy type cannot be null");
        }
        loanApplication.setFinalStrategy(type);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(tenureMonths);

        Loan loan = Loan.builder()
                .loanType(loanApplication.getLoanType())
                .loanApplication(loanApplication)
                .borrower(loanApplication.getBorrower())
                .approvedByOfficer(loanApplication.getReviewedByOfficer())
                .principalAmount(principalAmount)
                .interestRate(interestRate)
                .tenureMonths(tenureMonths)
                .strategyType(type)
                .startDate(startDate)
                .endDate(endDate)
                .status(LoanStatus.ACTIVE)
                .gracePeriodDays(loanProperties.getGracePeriodDays())
                .build();

//        loanRepository.save(loan);

        List<Emi> emiList=strategyFactory.getStrategy(type).generateSchedule(loan);
        if (emiList.isEmpty()) {
            throw new RuntimeException("EMI schedule generation failed");
        }
        loan.setEmiAmount(emiList.get(0).getEmiAmount());
        BigDecimal totalPayableAmount=BigDecimal.ZERO;

        for (Emi emi : emiList) {
            totalPayableAmount = totalPayableAmount.add(emi.getEmiAmount());
        }
        totalPayableAmount = totalPayableAmount.setScale(2, RoundingMode.HALF_UP);

        loan.setTotalPayableAmount(totalPayableAmount);

        loanRepository.save(loan);
        emiRepository.saveAll(emiList);

        return loanMapper.toResponse(loan);
    }

    @Override
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LoanResponse getLoanById(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));
        return loanMapper.toResponse(loan);
    }

    @Override
    public List<LoanResponse> getLoansByBorrowerId(Long borrowerId) {
        return loanRepository.findAllByBorrowerId(borrowerId)
                .stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> getLoansByType(LoanType loanType) {
        return loanRepository.findAllByLoanType(loanType)
                .stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> getLoansByStatus(LoanStatus status) {
        return loanRepository.findAllByStatus(status)
                .stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LoanResponse closeLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));

        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new RuntimeException("Loan is already closed");
        }

        boolean hasUnpaidEmis = emiRepository
                .existsByLoanIdAndStatusNot(loanId, EmiStatus.PAID);

        if (hasUnpaidEmis) {
            throw new RuntimeException("Cannot close loan. All EMIs must be paid.");
        }

        loan.setStatus(LoanStatus.CLOSED);
        loanRepository.save(loan);

        return loanMapper.toResponse(loan);
    }

}