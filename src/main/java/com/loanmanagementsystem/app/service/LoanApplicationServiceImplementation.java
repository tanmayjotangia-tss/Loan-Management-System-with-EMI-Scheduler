package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.request.LoanReviewRequest;
import com.loanmanagementsystem.app.dto.response.ApplyLoanResponse;
import com.loanmanagementsystem.app.dto.response.LoanApplicationForReviewResponse;
import com.loanmanagementsystem.app.dto.response.PendingLoanApplicationResponse;
import com.loanmanagementsystem.app.dto.response.ReviewedLoanApplicationResponse;
import com.loanmanagementsystem.app.entity.Borrower;
import com.loanmanagementsystem.app.entity.LoanApplication;
import com.loanmanagementsystem.app.entity.LoanOfficer;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.*;
import com.loanmanagementsystem.app.exception.BadRequestException;
import com.loanmanagementsystem.app.mapper.ApplyLoanMapper;
import com.loanmanagementsystem.app.mapper.LoanApplicationForReviewMapper;
import com.loanmanagementsystem.app.mapper.PendingLoanApplicationMapper;
import com.loanmanagementsystem.app.mapper.ReviewedLoanApplicationMapper;
import com.loanmanagementsystem.app.repository.*;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImplementation implements LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final BorrowerRepository borrowerRepository;
    private final LoanOfficerRepository loanOfficerRepository;
    private final LoanPropertiesRepository loanPropertiesRepository;
    private final ReviewedLoanApplicationMapper reviewedLoanApplicationMapper;
    private final ApplyLoanMapper applyLoanMapper;
    private final PendingLoanApplicationMapper pendingLoanApplicationMapper;
    private final LoanApplicationForReviewMapper loanApplicationForReviewMapper;
    private final NotificationService notificationService;
    private final LoanRepository loanRepository;
    private final AuditService auditService;



    @Override
    public ApplyLoanResponse applyForLoan(Long borrowerId, LoanApplicationRequest request) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new BadRequestException("Borrower not found"));

        LoanProperties loanProperties =loanPropertiesRepository.findByLoanType(request.getLoanType())
                .orElseThrow(() -> new BadRequestException("Loan properties not found"));

        if(!borrower.getIsVerified()){
            throw new BadRequestException("First Verify Your Documents");
        }

        if(loanRepository.findNumberOfActiveLoansByBorrowerId(borrowerId)>=3){
            throw new BadRequestException("You can't apply for more than 3 loans at a time.");
        }

        if (request.getRequestedAmount().compareTo(loanProperties.getMinAmount()) < 0
                || request.getRequestedAmount().compareTo(loanProperties.getMaxAmount()) > 0) {
            throw new IllegalArgumentException("Requested amount must be between " + loanProperties.getMinAmount() + " and " + loanProperties.getMaxAmount());
        }

        if (request.getRequestedTenureMonths() < loanProperties.getMinTenure()
                || request.getRequestedTenureMonths() > loanProperties.getMaxTenure()) {
            throw new IllegalArgumentException("Requested tenure must be between " + loanProperties.getMinTenure() + " and " + loanProperties.getMaxTenure() + " months");
        }

        LoanApplication loanApplication = applyLoanMapper.toEntity(request);
        loanApplication.setBorrower(borrower);

        BigDecimal dti=calculateDti(request);
        loanApplication.setCalculatedDti(dti);

        RiskCategory riskCategory = calculateRisk(dti,borrower.getCreditScore());

        loanApplication.setRiskCategory(riskCategory);
        StrategyType strategyType=decideStrategy(dti,request.getRequestedTenureMonths(),borrower.getCreditScore());

        LoanApplicationStatus status=LoanApplicationStatus.PENDING;
        if(strategyType==null){
            loanApplication.setStatus(LoanApplicationStatus.REJECTED);
            notificationService.sendNotification(
                    borrowerId,
                    NotificationType.APPLICATION,
                    "Loan Application Submitted",
                    "Your loan application for " + request.getRequestedAmount() + " has been Rejected."
            );
            loanApplicationRepository.save(loanApplication);
            auditService.logAction(borrower.getId(), EntityType.APPLICATION,loanApplication.getId(), AuditAction.CREATED,"Loan Application Created");
            return applyLoanMapper.toResponse(loanApplication);
        }

        loanApplication.setSuggestedStrategy(strategyType);
        loanApplication.setStatus(status);

        loanApplicationRepository.save(loanApplication);

        notificationService.sendNotification(
                borrowerId,
                NotificationType.APPLICATION,
                "Loan Application Submitted",
                "Your loan application for " + request.getRequestedAmount() + " has been successfully submitted and is under review."
        );
        auditService.logAction(borrower.getId(), EntityType.APPLICATION,loanApplication.getId(), AuditAction.CREATED,"Loan Application Created");
        return applyLoanMapper.toResponse(loanApplication);
    }

    private RiskCategory calculateRisk(BigDecimal dti, int creditScore) {

        if (creditScore == -1) {
            creditScore = 650;
        }
        if (creditScore < 550 || dti.compareTo(BigDecimal.valueOf(50)) > 0) {
            return RiskCategory.HIGH;
        }
        if (creditScore < 700 || dti.compareTo(BigDecimal.valueOf(30)) > 0) {
            return RiskCategory.MEDIUM;
        }

        return RiskCategory.LOW;
    }

    private StrategyType decideStrategy(BigDecimal dti, int tenureMonths, int creditScore) {

        if (creditScore == -1) {
            creditScore = 650;
        }
        if (creditScore < 550) {
            return null;
        }
        if (creditScore < 650) {
            if (dti.compareTo(BigDecimal.valueOf(30)) > 0) {
                return null;
            }
            return StrategyType.FLAT_RATE_LOAN;
        }

        if (creditScore < 750) {
            if (dti.compareTo(BigDecimal.valueOf(40)) <= 0) {
                return tenureMonths < 24
                        ? StrategyType.REDUCING_BALANCE_LOAN
                        : StrategyType.STEP_UP_EMI_LOAN;
            }
            return StrategyType.FLAT_RATE_LOAN;
        }

        if (dti.compareTo(BigDecimal.valueOf(20)) < 0) {
            return StrategyType.FLAT_RATE_LOAN;
        }

        if (dti.compareTo(BigDecimal.valueOf(40)) <= 0) {
            return tenureMonths < 24
                    ? StrategyType.REDUCING_BALANCE_LOAN
                    : StrategyType.STEP_UP_EMI_LOAN;
        }

        return null;
    }

    private BigDecimal calculateDti(LoanApplicationRequest request) {

        BigDecimal monthlyIncome = request.getMonthlyIncome();
        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monthly income must be greater than zero");
        }

        BigDecimal currentEmi = request.getCurrentEmi() == null
                ? BigDecimal.ZERO
                : request.getCurrentEmi();

        BigDecimal newLoanEmi = request.getRequestedAmount()
                .divide(BigDecimal.valueOf(request.getRequestedTenureMonths()), 6, RoundingMode.HALF_UP);

        BigDecimal totalDebt = currentEmi.add(newLoanEmi);

        return totalDebt
                .divide(monthlyIncome, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public ReviewedLoanApplicationResponse getApplicationById(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadRequestException("Loan application not found with id: " + applicationId));
        return reviewedLoanApplicationMapper.toResponse(application);
    }

    @Override
    public List<ReviewedLoanApplicationResponse> getCurrentUserApplications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        return loanApplicationRepository.findAllByBorrowerId(user.getUserId())
                .stream()
                .map(reviewedLoanApplicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReviewedLoanApplicationResponse> getApplicationsByBorrowerId(Long borrowerId) {
        return loanApplicationRepository.findAllByBorrowerId(borrowerId)
                .stream()
                .map(reviewedLoanApplicationMapper:: toResponse)
                .toList();
    }

    @Override
    public List<PendingLoanApplicationResponse> getAllPendingApplications() {
        return loanApplicationRepository.findAllByStatus(LoanApplicationStatus.PENDING)
                .stream()
                .map(pendingLoanApplicationMapper:: toResponse)
                .toList();
    }

    @Override
    public List<PendingLoanApplicationResponse> getPendingApplicationsByType(LoanType loanType) {
        return loanApplicationRepository.findAllByStatusAndLoanType(LoanApplicationStatus.PENDING,loanType)
                .stream()
                .map(pendingLoanApplicationMapper:: toResponse)
                .toList();
    }

    @Override
    public LoanApplicationForReviewResponse getApplicationForReview(Long applicationId) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdAndStatus(applicationId,LoanApplicationStatus.PENDING)
                .orElseThrow(() -> new BadRequestException("Loan application not found with id: " + applicationId));

        return loanApplicationForReviewMapper.toResponse(loanApplication);
    }

    @Override
    public ReviewedLoanApplicationResponse suggestStrategy(Long applicationId) {
        return null;
    }

    @Override
    public ReviewedLoanApplicationResponse reviewApplication(Long applicationId, Long officerId, LoanReviewRequest request) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdAndStatus(applicationId,LoanApplicationStatus.PENDING)
                .orElseThrow(() -> new BadRequestException("Loan application not found with id: " + applicationId));

        LoanOfficer loanOfficer = loanOfficerRepository.findById(officerId)
                .orElseThrow(() -> new BadRequestException("Officer not found with id: " + officerId));

        if(loanRepository.findNumberOfActiveLoansByBorrowerId(loanApplication.getBorrower().getId())>=3){
            throw new BadRequestException("User already have 3 active loans.");
        }
        loanApplication.setStatus(request.getStatus());
        loanApplication.setReviewedByOfficer(loanOfficer);
        loanApplication.setOfficerComment(request.getOfficerComment());
        loanApplication.setReviewedAt(LocalDateTime.now());

        if (request.getStatus() == LoanApplicationStatus.APPROVED) {
            if (request.getFinalStrategy() == null) {
                throw new BadRequestException("Final strategy is required for approval");
            }
            loanApplication.setFinalStrategy(request.getFinalStrategy());
        }

        loanApplicationRepository.save(loanApplication);

        NotificationType type = NotificationType.APPLICATION;
        if (request.getStatus() == LoanApplicationStatus.APPROVED) {
            type = NotificationType.APPROVAL;
        } else if (request.getStatus() == LoanApplicationStatus.REJECTED) {
            type = NotificationType.REJECTED;
        }

        notificationService.sendNotification(
                loanApplication.getBorrower().getId(),
                type,
                "Loan Application Update",
                "Your loan application status has been updated to: " + request.getStatus() + "."
        );
        auditService.logAction(loanOfficer.getId(), EntityType.APPLICATION,loanApplication.getId(), AuditAction.STATUS_CHANGED,"Loan Application Reviewed");
        return reviewedLoanApplicationMapper.toResponse(loanApplication);
    }
}
