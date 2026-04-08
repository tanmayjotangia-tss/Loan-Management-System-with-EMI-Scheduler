package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.entity.*;
import com.loanmanagementsystem.app.entity.enums.*;
import com.loanmanagementsystem.app.exception.AlreadyExistsException;
import com.loanmanagementsystem.app.exception.BadRequestException;
import com.loanmanagementsystem.app.factory.LoanStrategyFactory;
import com.loanmanagementsystem.app.mapper.LoanMapper;
import com.loanmanagementsystem.app.repository.*;
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
    private final CreditScoreService creditScoreService;
    private final BorrowerRepository borrowerRepository;
    private final AuditService auditService;

    @Transactional
    @Override
    public LoanResponse createLoanFromApplication(Long applicationId, StrategyType type) {
        LoanApplication loanApplication = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadRequestException("Loan application not found with id: " + applicationId));

//        Borrower borrower = borrowerRepository.findById(loanApplication.getBorrower().getId())
//                .orElseThrow(() -> new BadRequestException("Borrower not found"));

        Borrower borrower=loanApplication.getBorrower();

        if(borrower==null){
            throw new BadRequestException("Borrower not found");
        }

        int newCreditScore=creditScoreService.updateOnLoanCreation(borrower.getCreditScore(),loanApplication.getRequestedAmount());
        borrower.setCreditScore(newCreditScore);
        borrowerRepository.save(borrower);

        if (loanApplication.getStatus() != LoanApplicationStatus.APPROVED) {
            throw new BadRequestException("Loan application must be APPROVED to create a loan. Current status: " + loanApplication.getStatus());
        }

        if (loanRepository.findByLoanApplicationId(applicationId).isPresent()) {
            throw new AlreadyExistsException("Loan Application ", applicationId);
        }

        if(loanRepository.findNumberOfActiveLoansByBorrowerId(loanApplication.getBorrower().getId())>=3){
            throw new BadRequestException("User already have 3 active loans.");
        }

        LoanProperties loanProperties = loanPropertiesRepository.findByLoanType(loanApplication.getLoanType())
                .orElseThrow(() -> new BadRequestException("Loan properties not found for type: " + loanApplication.getLoanType()));

        BigDecimal interestRate = loanProperties.getInterestRate();
        BigDecimal principalAmount = loanApplication.getRequestedAmount();
        Integer tenureMonths = loanApplication.getRequestedTenureMonths();

        if(type==null){
            type=loanApplication.getSuggestedStrategy();
        }
        if (type == null) {
            throw new BadRequestException("Strategy type cannot be null");
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

        List<Emi> emiList=strategyFactory.getStrategy(type).generateSchedule(loan);
        if (emiList.isEmpty()) {
            throw new BadRequestException("EMI schedule generation failed");
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

        String loanInfo="Type: "+loan.getLoanType()+", Amount: "+loan.getTotalPayableAmount()+", Tenure Months: "+loan.getTenureMonths();
        auditService.logAction(borrower.getId(),EntityType.LOAN,loan.getId(),AuditAction.CREATED, loanInfo);

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
                .orElseThrow(() -> new BadRequestException("Loan not found with id: " + loanId));
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
                .orElseThrow(() -> new BadRequestException("Loan not found with id: " + loanId));

        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new BadRequestException("Loan is already closed");
        }

        boolean hasUnpaidEmis = emiRepository
                .existsByLoanIdAndStatusNot(loanId, EmiStatus.PAID);

        if (hasUnpaidEmis) {
            throw new BadRequestException("Cannot close loan. All EMIs must be paid.");
        }

        loan.setStatus(LoanStatus.CLOSED);
        loanRepository.save(loan);

        auditService.logAction(loan.getBorrower().getId(),EntityType.LOAN,loan.getId(),AuditAction.CREATED, "ACTIVE", "CLOSED");

        return loanMapper.toResponse(loan);
    }

}