package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.LoanApplicationRequest;
import com.loanmanagementsystem.app.entity.Borrower;
import com.loanmanagementsystem.app.entity.LoanApplication;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.enums.LoanApplicationStatus;
import com.loanmanagementsystem.app.entity.enums.LoanType;
import com.loanmanagementsystem.app.entity.enums.RiskCategory;
import com.loanmanagementsystem.app.entity.enums.StrategyType;
import com.loanmanagementsystem.app.mapper.ApplyLoanMapper;
import com.loanmanagementsystem.app.repository.BorrowerRepository;
import com.loanmanagementsystem.app.repository.LoanApplicationRepository;
import com.loanmanagementsystem.app.repository.LoanPropertiesRepository;
import com.loanmanagementsystem.app.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceImplementationTest {

    @Mock
    private BorrowerRepository borrowerRepository;
    @Mock
    private AuditService auditService;
    @Mock
    private LoanPropertiesRepository loanPropertiesRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private ApplyLoanMapper applyLoanMapper;
    @Mock
    private NotificationService notificationService;
    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private LoanApplicationServiceImplementation loanApplicationService;

    @Test
    void testRiskAndStrategyLogic_LowRisk_ReducingBalance() {
        Borrower borrower = new Borrower();
        borrower.setId(1L);
        borrower.setIsVerified(true);
        borrower.setCreditScore(800);

        LoanProperties properties = new LoanProperties();
        properties.setMinAmount(BigDecimal.valueOf(1000));
        properties.setMaxAmount(BigDecimal.valueOf(50000));
        properties.setMinTenure(6);
        properties.setMaxTenure(60);

        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setLoanType(LoanType.PERSONAL);
        request.setRequestedAmount(BigDecimal.valueOf(12000));
        request.setRequestedTenureMonths(12);
        request.setMonthlyIncome(BigDecimal.valueOf(10000));
        request.setCurrentEmi(BigDecimal.valueOf(1000));

        LoanApplication application = new LoanApplication();

        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(loanPropertiesRepository.findByLoanType(any())).thenReturn(Optional.of(properties));
        when(loanRepository.findNumberOfActiveLoansByBorrowerId(1L)).thenReturn(0L); // <-- Corrected
        when(applyLoanMapper.toEntity(request)).thenReturn(application);

        loanApplicationService.applyForLoan(1L, request);

        verify(loanApplicationRepository).save(application);
        assertEquals(RiskCategory.LOW, application.getRiskCategory());
        assertEquals(StrategyType.REDUCING_BALANCE_LOAN, application.getSuggestedStrategy());
        assertEquals(LoanApplicationStatus.PENDING, application.getStatus());
    }

    @Test
    void testRiskAndStrategyLogic_HighRisk_Rejected() {
        Borrower borrower = new Borrower();
        borrower.setId(1L);
        borrower.setIsVerified(true);
        borrower.setCreditScore(500);

        LoanProperties properties = new LoanProperties();
        properties.setMinAmount(BigDecimal.valueOf(1000));
        properties.setMaxAmount(BigDecimal.valueOf(50000));
        properties.setMinTenure(6);
        properties.setMaxTenure(60);

        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setLoanType(LoanType.PERSONAL);
        request.setRequestedAmount(BigDecimal.valueOf(12000));
        request.setRequestedTenureMonths(12);
        request.setMonthlyIncome(BigDecimal.valueOf(10000));
        request.setCurrentEmi(BigDecimal.valueOf(1000));

        LoanApplication application = new LoanApplication();

        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(loanPropertiesRepository.findByLoanType(any())).thenReturn(Optional.of(properties));
        when(loanRepository.findNumberOfActiveLoansByBorrowerId(1L)).thenReturn(0L); // <-- Corrected
        when(applyLoanMapper.toEntity(request)).thenReturn(application);

        loanApplicationService.applyForLoan(1L, request);

        verify(loanApplicationRepository).save(application);
        assertEquals(RiskCategory.HIGH, application.getRiskCategory());
        assertEquals(LoanApplicationStatus.REJECTED, application.getStatus());
    }
}