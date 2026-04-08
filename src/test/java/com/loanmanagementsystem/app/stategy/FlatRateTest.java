package com.loanmanagementsystem.app.stategy;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FlatRateTest {

    private final FlatRate flatRate = new FlatRate();

    @Test
    void testGenerateSchedule() {
        Loan loan = Loan.builder()
                .principalAmount(new BigDecimal("120000"))
                .interestRate(new BigDecimal("12")) // 12% annual
                .tenureMonths(12)
                .startDate(LocalDate.of(2026, 1, 1))
                .build();

        // Total Interest = (P * R * T) / 100 = (120,000 * 12 * 1) / 100 = 14,400
        // Total Payable = 134,400
        // EMI = 134,400 / 12 = 11,200

        List<Emi> schedule = flatRate.generateSchedule(loan);

        assertNotNull(schedule);
        assertEquals(12, schedule.size());

        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (Emi emi : schedule) {
            totalPrincipal = totalPrincipal.add(emi.getPrincipalComponent());
            totalInterest = totalInterest.add(emi.getInterestComponent());
            assertEquals(new BigDecimal("11200.00"), emi.getEmiAmount());
        }

        assertEquals(new BigDecimal("120000.00"), totalPrincipal.setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("14400.00"), totalInterest.setScale(2, RoundingMode.HALF_UP));
    }
}
