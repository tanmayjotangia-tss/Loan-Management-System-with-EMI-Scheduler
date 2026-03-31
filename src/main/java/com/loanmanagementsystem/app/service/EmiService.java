package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.EmiResponse;

import java.util.List;

public interface EmiService {
    Integer getTotalEmiByLoan(Long loanId);
    void markEmisPaid(Long loanId);
    Integer getTotalOverdueEmis(Long loanId);
}
