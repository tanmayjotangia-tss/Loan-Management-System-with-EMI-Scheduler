package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.dto.request.LoanReviewRequest;
import com.loanmanagementsystem.app.dto.response.LoanApplicationResponse;
import com.loanmanagementsystem.app.entity.Borrower;
import com.loanmanagementsystem.app.entity.LoanApplication;
import com.loanmanagementsystem.app.entity.LoanOfficer;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.LoanApplicationStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.entity.enums.NotificationType;
import com.loanmanagementsystem.app.mapper.LoanApplicationMapper;
import com.loanmanagementsystem.app.repository.BorrowerRepository;
import com.loanmanagementsystem.app.repository.LoanApplicationRepository;
import com.loanmanagementsystem.app.repository.LoanOfficerRepository;
import com.loanmanagementsystem.app.repository.LoanPropertiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImplementation implements LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final BorrowerRepository borrowerRepository;
    private final LoanOfficerRepository loanOfficerRepository;
    private final LoanPropertiesRepository loanPropertiesRepository;
    private final LoanApplicationMapper loanApplicationMapper;
    private final NotificationService notificationService;


    @Override
    public LoanApplicationResponse applyForLoan(Long borrowerId, LoanApplicationRequest request) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new RuntimeException("Borrower not found"));

        LoanProperties loanProperties =loanPropertiesRepository.findByLoanType(request.getLoanType())
                .orElseThrow(() -> new RuntimeException("Loan properties not found"));

        // Validate requested amount is within allowed range
        if (request.getRequestedAmount().compareTo(loanProperties.getMinAmount()) < 0
                || request.getRequestedAmount().compareTo(loanProperties.getMaxAmount()) > 0) {
            throw new RuntimeException("Requested amount must be between " + loanProperties.getMinAmount() + " and " + loanProperties.getMaxAmount());
        }

        // Validate requested tenure is within allowed range
        if (request.getRequestedTenureMonths() < loanProperties.getMinTenure()
                || request.getRequestedTenureMonths() > loanProperties.getMaxTenure()) {
            throw new RuntimeException("Requested tenure must be between " + loanProperties.getMinTenure() + " and " + loanProperties.getMaxTenure() + " months");
        }

        LoanApplication loanApplication = loanApplicationMapper.toEntity(request);
        loanApplication.setBorrower(borrower);

        //Calculate DTI

        //Calculate Risk

        loanApplicationRepository.save(loanApplication);

        notificationService.sendNotification(
                borrowerId,
                NotificationType.APPLICATION,
                "Loan Application Submitted",
                "Your loan application for " + request.getRequestedAmount() + " has been successfully submitted and is under review."
        );

        return loanApplicationMapper.toResponse(loanApplication);
    }

    @Override
    public LoanApplicationResponse getApplicationById(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + applicationId));
        return loanApplicationMapper.toResponse(application);
    }

    @Override
    public List<LoanApplicationResponse> getApplicationsByBorrowerId(Long borrowerId) {
        return loanApplicationRepository.findAllByBorrowerId(borrowerId)
                .stream()
                .map(loanApplicationMapper :: toResponse)
                .toList();
    }

    @Override
    public List<LoanApplicationResponse> getAllPendingApplications() {
        return loanApplicationRepository.findAllByStatus(LoanApplicationStatus.PENDING)
                .stream()
                .map(loanApplicationMapper :: toResponse)
                .toList();
    }

    @Override
    public List<LoanApplicationResponse> getPendingApplicationsByType(LoanType loanType) {
        return loanApplicationRepository.findAllByStatusAndLoanType(LoanApplicationStatus.PENDING,loanType)
                .stream()
                .map(loanApplicationMapper :: toResponse)
                .toList();
    }

    @Override
    public LoanApplicationResponse getApplicationForReview(Long applicationId) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdAndStatus(applicationId,LoanApplicationStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + applicationId));

        return loanApplicationMapper.toResponse(loanApplication);
    }

    @Override
    public LoanApplicationResponse suggestStrategy(Long applicationId) {
        return null;
    }

    @Override
    public LoanApplicationResponse reviewApplication(Long applicationId, Long officerId, LoanReviewRequest request) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdAndStatus(applicationId,LoanApplicationStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + applicationId));

        LoanOfficer loanOfficer = loanOfficerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found with id: " + officerId));

        loanApplication.setStatus(request.getStatus());
        loanApplication.setReviewedByOfficer(loanOfficer);
        loanApplication.setOfficerComment(request.getOfficerComment());
        loanApplication.setReviewedAt(LocalDateTime.now());

        if (request.getStatus() == LoanApplicationStatus.APPROVED && request.getFinalStrategy() != null) {
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

        return loanApplicationMapper.toResponse(loanApplication);
    }
}
