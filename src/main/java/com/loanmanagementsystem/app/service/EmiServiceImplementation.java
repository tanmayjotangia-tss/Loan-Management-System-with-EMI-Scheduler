package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.repository.EmiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
