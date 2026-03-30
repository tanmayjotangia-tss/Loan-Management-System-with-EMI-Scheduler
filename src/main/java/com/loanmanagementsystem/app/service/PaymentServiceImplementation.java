package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.PaymentRequest;
import com.loanmanagementsystem.app.dto.response.PaymentResponse;
import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.Payment;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import com.loanmanagementsystem.app.entity.enums.LoanStatus;
import com.loanmanagementsystem.app.entity.enums.NotificationType;
import com.loanmanagementsystem.app.mapper.PaymentMapper;
import com.loanmanagementsystem.app.repository.EmiRepository;
import com.loanmanagementsystem.app.repository.LoanRepository;
import com.loanmanagementsystem.app.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public PaymentResponse makeEmiPayment(PaymentRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + request.getLoanId()));

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

        // Validate the EMI belongs to this loan
        if (!emi.getLoan().getId().equals(loan.getId())) {
            throw new RuntimeException("EMI does not belong to the specified loan");
        }

        if (emi.getEmiAmount().compareTo(request.getAmountPaid()) > 0) {
            throw new RuntimeException("Amount paid is less than emi amount");
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

        loan.setTotalPayableAmount(loan.getTotalPayableAmount().subtract(request.getAmountPaid()));
        loanRepository.save(loan);

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

        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new RuntimeException("Cannot make payment on a closed loan");
        }

        LoanProperties loanProperties=loanPropertiesService.getLoanProperties(loan.getLoanType());
        if(loanProperties==null){
            throw new RuntimeException("Loan Properties Not Found.");
        }

        if(loanProperties.getMinEmiBeforeForeclosure()<=emiService.getTotalEmiByLoan(loan.getId())){
            throw new RuntimeException("Minimum "+loanProperties.getMinEmiBeforeForeclosure()+" EMIs required.");
        }


        if(loan.getTotalPayableAmount().compareTo(request.getAmountPaid()) > 0) {
            throw new RuntimeException("Amount paid is less than emi amount");
        }

        // check payment >= totalPendingPayment -> one new entity surplusAmount

        Payment payment = paymentMapper.toEntity(request);
        payment.setLoan(loan);
        payment.setEmi(null);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        // Mark all emi&penalties paid

        return paymentMapper.toResponse(payment);
    }

    //one function to getTotalPendingAmountForLoanId

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

    @Override
    public Long getTotalPendingPayment(Long loanId) {
        return 0L;
    }
}
