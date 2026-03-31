package com.loanmanagementsystem.app.stategy;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StepUp implements LoanStrategy {

    private static final BigDecimal STEP_UP_RATE = new BigDecimal("0.10"); // 10% increase
    private static final int STEP_PERIOD = 12; // every 12 months

    @Override
    public List<Emi> generateSchedule(Loan loan) {

        List<Emi> emis = new ArrayList<>();

        BigDecimal principal = loan.getPrincipalAmount();
        BigDecimal annualRate = loan.getInterestRate();
        int tenure = loan.getTenureMonths();

        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal emiAmount = calculateInitialEmi(principal, monthlyRate, tenure);

        BigDecimal remainingPrincipal = principal;
        LocalDate dueDate = loan.getStartDate().plusMonths(1);

        for (int i = 1; i <= tenure; i++) {

            if (i > 1 && (i - 1) % STEP_PERIOD == 0) {
                emiAmount = emiAmount.multiply(BigDecimal.ONE.add(STEP_UP_RATE))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal interest = remainingPrincipal.multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principalComponent = emiAmount.subtract(interest)
                    .setScale(2, RoundingMode.HALF_UP);

            if (i == tenure || principalComponent.compareTo(remainingPrincipal) > 0) {
                principalComponent = remainingPrincipal;
                interest = remainingPrincipal.multiply(monthlyRate)
                        .setScale(2, RoundingMode.HALF_UP);
                emiAmount = principalComponent.add(interest);
            }

            Emi emi = buildEmi(
                    loan,
                    i,
                    dueDate,
                    principalComponent,
                    interest,
                    emiAmount
            );

            emis.add(emi);

            remainingPrincipal = remainingPrincipal.subtract(principalComponent)
                    .setScale(2, RoundingMode.HALF_UP);

            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO;
            }

            dueDate = dueDate.plusMonths(1);
        }

        return emis;
    }

    private BigDecimal calculateInitialEmi(BigDecimal principal, BigDecimal monthlyRate, int tenure) {

        BigDecimal onePlusRPowerN = (BigDecimal.ONE.add(monthlyRate)).pow(tenure);

        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
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