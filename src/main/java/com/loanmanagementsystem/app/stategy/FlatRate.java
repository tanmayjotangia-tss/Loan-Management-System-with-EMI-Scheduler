package com.loanmanagementsystem.app.stategy;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlatRate implements LoanStrategy{
    @Override
    public List<Emi> generateSchedule(Loan loan) {

        List<Emi> emis = new ArrayList<>();

        BigDecimal principal = loan.getPrincipalAmount();
        BigDecimal annualRate = loan.getInterestRate();
        int tenure = loan.getTenureMonths();

        BigDecimal totalInterest = calculateTotalInterest(principal, annualRate, tenure);
        BigDecimal totalPayable = principal.add(totalInterest);

        BigDecimal emiAmount = totalPayable.divide(BigDecimal.valueOf(tenure), 2, RoundingMode.HALF_UP);
        BigDecimal principalPerEmi = principal.divide(BigDecimal.valueOf(tenure), 2, RoundingMode.HALF_UP);
        BigDecimal interestPerEmi = totalInterest.divide(BigDecimal.valueOf(tenure), 2, RoundingMode.HALF_UP);

        LocalDate dueDate = loan.getStartDate().plusMonths(1);

        for (int i = 1; i <= tenure; i++) {

            Emi emi;

            if (i == tenure) {
                BigDecimal paidPrincipal = principalPerEmi.multiply(BigDecimal.valueOf(tenure - 1));
                BigDecimal lastPrincipal = principal.subtract(paidPrincipal);

                BigDecimal paidInterest = interestPerEmi.multiply(BigDecimal.valueOf(tenure - 1));
                BigDecimal lastInterest = totalInterest.subtract(paidInterest);

                emi = buildEmi(loan, i, dueDate, lastPrincipal, lastInterest, lastPrincipal.add(lastInterest));
            } else {
                emi = buildEmi(loan, i, dueDate, principalPerEmi, interestPerEmi, emiAmount);
            }

            emis.add(emi);
            dueDate = dueDate.plusMonths(1);
        }

        return emis;
    }

    private BigDecimal calculateTotalInterest(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        BigDecimal rate = annualRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal time = BigDecimal.valueOf(tenureMonths).divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        return principal.multiply(rate).multiply(time).setScale(2, RoundingMode.HALF_UP);
    }

    private Emi buildEmi(Loan loan,
                         int installmentNo,
                         LocalDate dueDate,
                         BigDecimal principal,
                         BigDecimal interest,
                         BigDecimal emiAmount) {

        return Emi.builder()
                .loan(loan)
                .installmentNumber(installmentNo)
                .dueDate(dueDate)
                .principalComponent(principal)
                .interestComponent(interest)
                .emiAmount(emiAmount)
                .build();
    }
}
