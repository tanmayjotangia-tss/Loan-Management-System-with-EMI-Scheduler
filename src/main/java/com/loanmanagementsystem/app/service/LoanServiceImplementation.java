package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;
import com.loanmanagementsystem.app.entity.LoanApplication;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import com.loanmanagementsystem.app.entity.enums.LoanApplicationStatus;
import com.loanmanagementsystem.app.entity.enums.LoanStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.mapper.LoanMapper;
import com.loanmanagementsystem.app.repository.EmiRepository;
import com.loanmanagementsystem.app.repository.LoanApplicationRepository;
import com.loanmanagementsystem.app.repository.LoanPropertiesRepository;
import com.loanmanagementsystem.app.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Override
    public LoanResponse createLoanFromApplication(Long applicationId) {
        LoanApplication loanApplication = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + applicationId));

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

        // Strategy

        // EMI

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
//                .emiAmount(emiAmount)
//                .strategyType(strategyType)
//                .totalPayableAmount(totalPayableAmount)
                .startDate(startDate)
                .endDate(endDate)
                .status(LoanStatus.ACTIVE)
                .gracePeriodDays(loanProperties.getGracePeriodDays())
                .build();

        loanRepository.save(loan);

        // Generate EMI schedule

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
    public LoanResponse closeLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));

        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new RuntimeException("Loan is already closed");
        }

        List<Emi> pendingEmis = emiRepository.findAllByLoanIdAndStatus(loanId, EmiStatus.PENDING);
        List<Emi> overdueEmis = emiRepository.findAllByLoanIdAndStatus(loanId, EmiStatus.OVERDUE);

        if (!pendingEmis.isEmpty() || !overdueEmis.isEmpty()) {
            throw new RuntimeException("Cannot close loan. There are " + pendingEmis.size() + " pending and " + overdueEmis.size() + " overdue EMIs remaining");
        }

        loan.setStatus(LoanStatus.CLOSED);
        loanRepository.save(loan);
        return loanMapper.toResponse(loan);
    }

    @Override
    public LoanResponse processForeclosure(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));



        return loanMapper.toResponse(loan);
    }

}