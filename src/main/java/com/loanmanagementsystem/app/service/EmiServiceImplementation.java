package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.EmiResponse;
import com.loanmanagementsystem.app.dto.response.LoanResponse;
import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import com.loanmanagementsystem.app.repository.EmiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmiServiceImplementation implements EmiService{

    private final EmiRepository emiRepository;

    @Override
    public Integer getTotalEmiByLoan(Long loanId) {

        List<Emi> emis= emiRepository.findAllByLoanIdOrderByInstallmentNumberAsc(loanId);

        if(emis==null) {
            return 0;
        }
        return emis.size();
    }

    @Override
    public void markEmisPaid(Long loanId) {
        List<Emi> unpaidEmis=emiRepository.findUnpaidEmisByLoanId(loanId);

        if(unpaidEmis==null){
            return;
        }
        for(Emi emi:unpaidEmis){
            emi.setStatus(EmiStatus.PAID);
            emi.setPaymentDate(LocalDate.now());
            emi.setPaidAmount(emi.getEmiAmount());
            emiRepository.save(emi);
        }
    }

}
