package com.loanmanagementsystem.app.service;

public interface EmiService {
    Integer getTotalUnpaidEmiByLoan(Long loanId);
    void markEmisPaid(Long loanId);
    Integer getTotalOverdueEmis(Long loanId);
}
