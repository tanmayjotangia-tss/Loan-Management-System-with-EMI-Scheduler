package com.loanmanagementsystem.app.service;

import java.math.BigDecimal;
import java.util.List;

import com.loanmanagementsystem.app.dto.response.PenaltyResponse;
import com.loanmanagementsystem.app.entity.enums.PenaltyReason;

public interface PenaltyService {
    PenaltyResponse applyPenalty(Long emiId, PenaltyReason reason);
    List<PenaltyResponse> getPenaltiesByLoanId(Long loanId);
    List<PenaltyResponse> getUnpaidPenaltiesByLoanId(Long loanId);
    BigDecimal getTotalPendingPenalties(Long loanId);
    List<PenaltyResponse> getPenaltiesByEmi(Long emiId);
    void markPenaltiesPaid(Long loanId);
}
