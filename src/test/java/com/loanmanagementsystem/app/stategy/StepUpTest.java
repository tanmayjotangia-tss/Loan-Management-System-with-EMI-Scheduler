package com.loanmanagementsystem.app.stategy;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StepUpTest {

    private final StepUp stepUp = new StepUp();

    @Test
    void testGenerateScheduleWithStepUp() {
        Loan loan = Loan.builder()
                .principalAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("12"))
                .tenureMonths(24)
                .startDate(LocalDate.of(2026, 1, 1))
                .build();

        List<Emi> schedule = stepUp.generateSchedule(loan);

        assertEquals(24, schedule.size());

        BigDecimal firstEmi = schedule.get(0).getEmiAmount();
        BigDecimal thirteenthEmi = schedule.get(12).getEmiAmount();

        BigDecimal expectedThirteenthEmi = firstEmi.multiply(new BigDecimal("1.10")).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedThirteenthEmi, thirteenthEmi, "EMI should increase by 10% after 12 months");

        BigDecimal totalPrincipal = BigDecimal.ZERO;
        for (Emi emi : schedule) {
            totalPrincipal = totalPrincipal.add(emi.getPrincipalComponent());
        }

        assertEquals(new BigDecimal("100000.00"), totalPrincipal.setScale(2, RoundingMode.HALF_UP));
    }
}
