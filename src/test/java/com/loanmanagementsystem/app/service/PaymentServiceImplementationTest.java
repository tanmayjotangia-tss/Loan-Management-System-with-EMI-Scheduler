package com.loanmanagementsystem.app.service;

import static org.junit.jupiter.api.Assertions.*;

import com.loanmanagementsystem.app.dto.request.PaymentRequest;
import com.loanmanagementsystem.app.dto.response.EmiResponse;
import com.loanmanagementsystem.app.dto.response.PaymentResponse;
import com.loanmanagementsystem.app.entity.*;
import com.loanmanagementsystem.app.entity.enums.*;
import com.loanmanagementsystem.app.mapper.PaymentMapper;
import com.loanmanagementsystem.app.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplementationTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private LoanRepository loanRepository;
    @Mock private EmiRepository emiRepository;
    @Mock private EmiService emiService;
    @Mock private LoanPropertiesService loanPropertiesService;
    @Mock private PaymentMapper paymentMapper;
    @Mock private NotificationService notificationService;
    @Mock private PenaltyService penaltyService;
    @Mock private BorrowerRepository borrowerRepository;
    @Mock private CreditScoreService creditScoreService;
    @Mock private AuditService auditService;

    @InjectMocks
    private PaymentServiceImplementation paymentService;


    @Test
    void makeEmiPayment_success() {
        Long loanId = 1L;
        Long installmentNumber = 1L;

        Borrower borrower = new Borrower();
        borrower.setId(1L);
        borrower.setSurplusAmount(new BigDecimal("1000"));
        borrower.setCreditScore(700);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setBorrower(borrower);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setTotalPayableAmount(new BigDecimal("10000"));

        Emi emi = new Emi();
        emi.setId(10L);
        emi.setLoan(loan);
        emi.setStatus(EmiStatus.PENDING);
        emi.setEmiAmount(new BigDecimal("2000"));

        PaymentRequest request = new PaymentRequest();
        request.setLoanId(loanId);
        request.setInstallmentNumber(installmentNumber);
        request.setAmountPaid(new BigDecimal("2500"));
        request.setPaymentMode(PaymentMode.UPI);

        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(emiRepository.findByLoanIdAndInstallmentNumber(loanId, installmentNumber)).thenReturn(Optional.of(emi));
        when(penaltyService.getTotalPendingPenalties(loanId)).thenReturn(BigDecimal.ZERO);
        when(paymentMapper.toEntity(request)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(response);
        when(creditScoreService.updateOnPayment(anyInt(), any())).thenReturn(750);

        PaymentResponse result = paymentService.makeEmiPayment(request);

        assertNotNull(result);
        verify(paymentRepository).save(payment);
        verify(emiRepository).save(emi);
        verify(loanRepository).save(loan);
        verify(borrowerRepository).save(borrower);
        verify(penaltyService).markPenaltiesPaid(loanId);
        verify(notificationService).sendNotification(any(), any(), any(), any());
        verify(auditService).logAction(anyLong(), any(), anyLong(), any(), any(), any());
    }

    @Test
    void makeEmiPayment_loanClosed_shouldThrow() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.CLOSED);

        PaymentRequest request = new PaymentRequest();
        request.setLoanId(1L);
        request.setInstallmentNumber(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(RuntimeException.class,
                () -> paymentService.makeEmiPayment(request));
    }

    @Test
    void makeEmiPayment_emiAlreadyPaid_shouldThrow() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.ACTIVE);

        Emi emi = new Emi();
        emi.setStatus(EmiStatus.PAID);
        emi.setLoan(loan);

        PaymentRequest request = new PaymentRequest();
        request.setLoanId(1L);
        request.setInstallmentNumber(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(emiRepository.findByLoanIdAndInstallmentNumber(1L, 1L)).thenReturn(Optional.of(emi));

        assertThrows(RuntimeException.class,
                () -> paymentService.makeEmiPayment(request));
    }

    @Test
    void makeForeclosurePayment_success() {
        Long loanId = 1L;

        Borrower borrower = new Borrower();
        borrower.setSurplusAmount(BigDecimal.ZERO);
        borrower.setCreditScore(700);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setBorrower(borrower);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setTotalPayableAmount(new BigDecimal("5000"));

        LoanProperties props = new LoanProperties();
        props.setMinEmiBeforeForeclosure(5);
        props.setForeclosurePenaltyPercent(new BigDecimal("10"));
        props.setForeclosureAllowed(true);

        BigDecimal totalUnpaidEmi = new BigDecimal("5000"); // from EmiResponse
        BigDecimal foreclosurePenalty = totalUnpaidEmi.multiply(props.getForeclosurePenaltyPercent())
                .divide(new BigDecimal("100"));

        BigDecimal requiredAmount = totalUnpaidEmi.add(foreclosurePenalty);

        PaymentRequest request = new PaymentRequest();
        request.setLoanId(loanId);
        request.setAmountPaid(requiredAmount);

        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanPropertiesService.getLoanProperties(any())).thenReturn(props);
        when(emiService.getTotalUnpaidEmiByLoan(loanId)).thenReturn(10);
        when(emiService.getUnpaidEmis(loanId)).thenReturn(List.of(
                EmiResponse.builder().principalComponent(new BigDecimal("5000")).build()
        ));
        when(penaltyService.getTotalPendingPenalties(loanId)).thenReturn(BigDecimal.ZERO);
        when(paymentMapper.toEntity(request)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(response);
        when(creditScoreService.updateOnForeclosure(anyInt(), anyInt(), any())).thenReturn(750);

        PaymentResponse result = paymentService.makeForeclosurePayment(request);

        assertNotNull(result);
        verify(paymentRepository).save(payment);
        verify(loanRepository).save(loan);
        verify(emiService).markEmisPaid(loanId);
        verify(borrowerRepository).save(borrower);
        verify(penaltyService).markPenaltiesPaid(loanId);
        verify(auditService).logAction(
                Mockito.<Long>nullable(Long.class),
                any(),
                anyLong(),
                any(),
                any(),
                any()
        );
    }

    @Test
    void getPaymentsByEmiId() {
        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse();

        when(paymentRepository.findAllByEmiId(1L))
                .thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment))
                .thenReturn(response);

        List<PaymentResponse> result = paymentService.getPaymentsByEmiId(1L);

        assertEquals(1, result.size());
    }
}