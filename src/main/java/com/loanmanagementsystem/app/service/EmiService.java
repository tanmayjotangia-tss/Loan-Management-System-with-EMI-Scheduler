package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.EmiResponse;

import java.util.List;

public interface EmiService {
    Integer getTotalUnpaidEmiByLoan(Long loanId);
    void markEmisPaid(Long loanId);
    Integer getTotalOverdueEmis(Long loanId);
    List<EmiResponse> getUnpaidEmis(Long loanId);
    List<EmiResponse> getAllEmis(Long loanId);
}
