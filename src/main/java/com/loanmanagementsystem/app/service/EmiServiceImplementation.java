package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.EmiResponse;
import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import com.loanmanagementsystem.app.mapper.EmiMapper;
import com.loanmanagementsystem.app.repository.EmiRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmiServiceImplementation implements EmiService{

    private final EmiRepository emiRepository;
    private final EmiMapper emiMapper;

    @Override
    public Integer getTotalUnpaidEmiByLoan(Long loanId) {

        List<Emi> emis= emiRepository.findAllActiveEmisByLoanId(loanId);

        if(emis==null) {
            return 0;
        }
        return emis.size();
    }

    @Override
    @Transactional
    public void markEmisPaid(Long loanId) {
        List<Emi> unpaidEmis=emiRepository.findUnpaidEmisByLoanId(loanId);

        if(unpaidEmis==null){
            return;
        }
        for(Emi emi:unpaidEmis){
            emi.setStatus(EmiStatus.PAID);
            emi.setPaymentDate(LocalDate.now());
            emi.setPaidAmount(emi.getEmiAmount());
        }
        emiRepository.saveAll(unpaidEmis);
    }

    @Override
    public Integer getTotalOverdueEmis(Long loanId){
        List<Emi> overdueEmis=emiRepository.findAllByLoanIdAndStatus(loanId,EmiStatus.OVERDUE);

        if(overdueEmis==null){
            return 0;
        }
        return overdueEmis.size();
    }

    @Override
    public List<EmiResponse> getUnpaidEmis(Long loanId) {

        List<Emi> unpaidEmis=emiRepository.findUnpaidEmisByLoanId(loanId);

        return unpaidEmis.stream().map(emiMapper::toResponse).toList();
    }

    @Override
    public List<EmiResponse> getAllEmis(Long loanId) {
        List<Emi> unpaidEmis=emiRepository.findAllByLoanId(loanId);

        return unpaidEmis.stream().map(emiMapper::toResponse).toList();
    }

}
