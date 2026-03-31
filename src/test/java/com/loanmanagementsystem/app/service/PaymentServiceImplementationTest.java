package com.loanmanagementsystem.app.service;

import static org.junit.jupiter.api.Assertions.*;

import com.loanmanagementsystem.app.dto.request.PaymentRequest;
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

import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    private PaymentServiceImplementation paymentService;


    @Test
    void makeEmiPayment_success() {
        Long loanId = 1L;
        Long emiId = 10L;

        Borrower borrower = new Borrower();
        borrower.setId(1L);
        borrower.setSurplusAmount(new BigDecimal("1000"));

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setBorrower(borrower);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setTotalPayableAmount(new BigDecimal("10000"));

        Emi emi = new Emi();
        emi.setId(emiId);
        emi.setLoan(loan);
        emi.setStatus(EmiStatus.PENDING);
        emi.setEmiAmount(new BigDecimal("2000"));

        PaymentRequest request = new PaymentRequest();
        request.setLoanId(loanId);
        request.setEmiId(emiId);
        request.setAmountPaid(new BigDecimal("1500"));

        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(emiRepository.findById(emiId)).thenReturn(Optional.of(emi));
        when(penaltyService.getTotalPendingPenalties(loanId)).thenReturn(BigDecimal.ZERO);
        when(paymentMapper.toEntity(request)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.makeEmiPayment(request);

        assertNotNull(result);
        verify(paymentRepository).save(payment);
        verify(emiRepository).save(emi);
        verify(loanRepository).save(loan);
        verify(notificationService).sendNotification(any(), any(), any(), any());
    }

    @Test
    void makeEmiPayment_loanClosed_shouldThrow() {
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.CLOSED);

        PaymentRequest request = new PaymentRequest();
        request.setLoanId(1L);
        request.setEmiId(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(RuntimeException.class,
                () -> paymentService.makeEmiPayment(request));
    }

    // ===============================
    // EMI PAYMENT - EMI ALREADY PAID
    // ===============================

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
        request.setEmiId(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(emiRepository.findById(1L)).thenReturn(Optional.of(emi));

        assertThrows(RuntimeException.class,
                () -> paymentService.makeEmiPayment(request));
    }

    @Test
    void makeForeclosurePayment_success() {
        Long loanId = 1L;

        Borrower borrower = new Borrower();
        borrower.setSurplusAmount(BigDecimal.ZERO);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setBorrower(borrower);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setTotalPayableAmount(new BigDecimal("5000"));

        LoanProperties props = new LoanProperties();
        props.setMinEmiBeforeForeclosure(5);
        props.setForeclosurePenaltyPercent(new BigDecimal("0.1"));

        PaymentRequest request = new PaymentRequest();
        request.setLoanId(loanId);
        request.setAmountPaid(new BigDecimal("1000"));

        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanPropertiesService.getLoanProperties(any())).thenReturn(props);
        when(emiService.getTotalEmiByLoan(loanId)).thenReturn(10);
        when(penaltyService.getTotalPendingPenalties(loanId)).thenReturn(BigDecimal.ZERO);
        when(paymentMapper.toEntity(request)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.makeForeclosurePayment(request);

        assertNotNull(result);
        verify(paymentRepository).save(payment);
        verify(loanRepository).save(loan);
        verify(emiService).markEmisPaid(loanId);
    }

    @Test
    void getPaymentsByLoanId() {
        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse();

        when(paymentRepository.findAllByLoanId(1L))
                .thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment))
                .thenReturn(response);

        List<PaymentResponse> result = paymentService.getPaymentsByLoanId(1L);

        assertEquals(1, result.size());
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