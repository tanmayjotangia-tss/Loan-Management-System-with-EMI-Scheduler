package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.PaymentRequest;
import com.loanmanagementsystem.app.dto.response.PaymentResponse;
import com.loanmanagementsystem.app.entity.*;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import com.loanmanagementsystem.app.entity.enums.LoanStatus;
import com.loanmanagementsystem.app.entity.enums.NotificationType;
import com.loanmanagementsystem.app.mapper.PaymentMapper;
import com.loanmanagementsystem.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImplementation implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;
    private final EmiRepository emiRepository;
    private final EmiService emiService;
    private final LoanPropertiesService loanPropertiesService;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;
    private final PenaltyService penaltyService;
    private BorrowerRepository borrowerRepository;

    @Override
    public PaymentResponse makeEmiPayment(PaymentRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + request.getLoanId()));

        Borrower borrower = loan.getBorrower();

        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new RuntimeException("Cannot make payment on a closed loan");
        }

        if (request.getEmiId() == null) {
            throw new RuntimeException("EMI ID is required for EMI payment");
        }

        Emi emi = emiRepository.findById(request.getEmiId())
                .orElseThrow(() -> new RuntimeException("EMI not found with id: " + request.getEmiId()));

        if (emi.getStatus() == EmiStatus.PAID) {
            throw new RuntimeException("EMI is already paid");
        }

        if (!emi.getLoan().getId().equals(loan.getId())) {
            throw new RuntimeException("EMI does not belong to the specified loan");
        }

        BigDecimal totalAvailableAmount = request.getAmountPaid().add(borrower.getSurplusAmount());
        BigDecimal payableAmount = emi.getEmiAmount().add(penaltyService.getTotalPendingPenalties(loan.getId()));

        if (totalAvailableAmount.compareTo(payableAmount) >= 0) {
            throw new RuntimeException("Amount paid is less than needed amount");
        }

        Payment payment = paymentMapper.toEntity(request);
        payment.setLoan(loan);
        payment.setEmi(emi);
        payment.setPaymentMode(request.getPaymentMode());
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        emi.setStatus(EmiStatus.PAID);
        emi.setPaymentDate(LocalDate.now());
        emi.setPaidAmount(request.getAmountPaid());
        emiRepository.save(emi);

        loan.setTotalPayableAmount(loan.getTotalPayableAmount().subtract(emi.getEmiAmount()));
        loanRepository.save(loan);

        borrower.setSurplusAmount(totalAvailableAmount.subtract(payableAmount));
        borrowerRepository.save(borrower);
        penaltyService.markPenaltiesPaid(loan.getId());

        notificationService.sendNotification(
                loan.getBorrower().getId(),
                NotificationType.PAYMENT,
                "EMI Payment Successful",
                "Your EMI payment of " + request.getAmountPaid() + " for Loan ID " + loan.getId() + " was successful."
        );

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse makeForeclosurePayment(PaymentRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + request.getLoanId()));

        Borrower borrower = loan.getBorrower();

        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new RuntimeException("Cannot make payment on a closed loan");
        }

        LoanProperties loanProperties = loanPropertiesService.getLoanProperties(loan.getLoanType());
        if (loanProperties == null) {
            throw new RuntimeException("Loan Properties Not Found.");
        }

        if (loanProperties.getMinEmiBeforeForeclosure() <= emiService.getTotalEmiByLoan(loan.getId())) {
            throw new RuntimeException("Minimum " + loanProperties.getMinEmiBeforeForeclosure() + " EMIs required.");
        }

        BigDecimal chargeOnForeclosure = loan.getTotalPayableAmount().multiply(loanProperties.getForeclosurePenaltyPercent());
        BigDecimal totalPayableAmount = loan.getTotalPayableAmount().add(penaltyService.getTotalPendingPenalties(loan.getId())).add(chargeOnForeclosure);
        BigDecimal totalAvailableAmount = borrower.getSurplusAmount().add(request.getAmountPaid());

        if (totalAvailableAmount.compareTo(totalPayableAmount) >= 0) {
            throw new RuntimeException("Amount paid is less than required amount");
        }

        Payment payment = paymentMapper.toEntity(request);
        payment.setLoan(loan);
        payment.setEmi(null);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        loan.setTotalPayableAmount(BigDecimal.ZERO);
        loanRepository.save(loan);

        borrower.setSurplusAmount(totalAvailableAmount.subtract(totalPayableAmount));
        borrowerRepository.save(borrower);
        penaltyService.markPenaltiesPaid(loan.getId());

        emiService.markEmisPaid(loan.getId());

        return paymentMapper.toResponse(payment);
    }


    @Override
    public List<PaymentResponse> getPaymentsByLoanId(Long loanId) {
        return paymentRepository.findAllByLoanId(loanId)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByEmiId(Long emiId) {
        return paymentRepository.findAllByEmiId(emiId)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

}
