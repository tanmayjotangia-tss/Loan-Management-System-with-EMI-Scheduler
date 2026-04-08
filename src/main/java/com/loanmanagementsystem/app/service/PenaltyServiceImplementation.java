package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.PenaltyResponse;
import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;
import com.loanmanagementsystem.app.entity.LoanProperties;
import com.loanmanagementsystem.app.entity.Penalty;
import com.loanmanagementsystem.app.entity.enums.PenaltyReason;
import com.loanmanagementsystem.app.exception.BadRequestException;
import com.loanmanagementsystem.app.mapper.PenaltyMapper;
import com.loanmanagementsystem.app.repository.EmiRepository;
import com.loanmanagementsystem.app.repository.LoanPropertiesRepository;
import com.loanmanagementsystem.app.repository.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyServiceImplementation implements PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final EmiRepository emiRepository;
    private final LoanPropertiesRepository loanPropertiesRepository;
    private final PenaltyMapper penaltyMapper;

    @Override
    public PenaltyResponse applyPenalty(Long emiId, PenaltyReason reason) {
        Emi emi = emiRepository.findById(emiId)
                .orElseThrow(() -> new BadRequestException("EMI not found with id: " + emiId));

        Loan loan = emi.getLoan();

        LoanProperties loanProperties = loanPropertiesRepository.findByLoanType(loan.getLoanType())
                .orElseThrow(() -> new BadRequestException("Loan properties not found for type: " + loan.getLoanType()));

        BigDecimal penaltyPercentage= BigDecimal.valueOf(0.0);
        if (reason == PenaltyReason.LATE_PAYMENT) {
            penaltyPercentage = loanProperties.getLatePaymentPenaltyPercentage();
        }
        else if(reason == PenaltyReason.MISSED_EMI)
        {
            penaltyPercentage = loanProperties.getMissedEmiPenaltyPercentage();
        }

        BigDecimal penaltyAmount = emi.getEmiAmount()
                .multiply(penaltyPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        Penalty penalty = Penalty.builder()
                .emi(emi)
                .loan(loan)
                .amount(penaltyAmount)
                .reason(reason)
                .appliedDate(LocalDate.now())
                .isPaid(false)
                .build();

        penaltyRepository.save(penalty);
        return penaltyMapper.toResponse(penalty);
    }

    @Override
    public List<PenaltyResponse> getPenaltiesByLoanId(Long loanId) {
        return penaltyRepository.findAllByLoanId(loanId)
                .stream()
                .map(penaltyMapper :: toResponse)
                .toList();
    }

    public void markPenaltiesPaid(Long loanId){
        List<Penalty> penalties=penaltyRepository.findAllByLoanIdAndIsPaidFalse(loanId);

        if(penalties==null){
            return;
        }
        for(Penalty penalty:penalties){
            penalty.setIsPaid(true);
            penalty.setPaidDate(LocalDate.now());
        }
        penaltyRepository.saveAll(penalties);
    }

    @Override
    public List<PenaltyResponse> getUnpaidPenaltiesByLoanId(Long loanId) {
        return penaltyRepository.findAllByLoanIdAndIsPaidFalse(loanId)
                .stream()
                .map(penaltyMapper :: toResponse)
                .toList();
    }

    @Override
    public List<PenaltyResponse> getPenaltiesByEmi(Long emiId) {
        return penaltyRepository.findAllByEmiId(emiId)
                .stream()
                .map(penaltyMapper :: toResponse)
                .toList();
    }

    public BigDecimal getTotalPendingPenalties(Long loanId) {

        List<Penalty> pendingPenalties=penaltyRepository.findAllByLoanIdAndIsPaidFalse(loanId);

        BigDecimal totalPenalties=BigDecimal.ZERO;

        if(pendingPenalties==null){
            return totalPenalties;
        }
        for(Penalty penalty:pendingPenalties){
            totalPenalties=totalPenalties.add(penalty.getAmount());
        }
        return totalPenalties;
    }
}
