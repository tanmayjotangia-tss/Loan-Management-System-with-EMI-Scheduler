package com.loanmanagementsystem.app.stategy;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReducingBalanceTest {

    private final ReducingBalance reducingBalance = new ReducingBalance();

    @Test
    void testGenerateSchedule() {
        Loan loan = Loan.builder()
                .principalAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("12")) // 12% annual -> 1% monthly
                .tenureMonths(12)
                .startDate(LocalDate.of(2026, 1, 1))
                .build();

        // Formula: EMI = [P x R x (1+R)^N]/[(1+R)^N-1]
        // EMI = [100000 * 0.01 * (1.01)^12] / [(1.01)^12 - 1] = 8884.88 (approximately)

        List<Emi> schedule = reducingBalance.generateSchedule(loan);


        assertEquals(12, schedule.size());

        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal firstInterest = schedule.get(0).getInterestComponent();
        BigDecimal lastInterest = schedule.get(11).getInterestComponent();

        for (Emi emi : schedule) {
            totalPrincipal = totalPrincipal.add(emi.getPrincipalComponent());
        }

        assertTrue(firstInterest.compareTo(lastInterest) > 0, "Interest component should decrease over time");
        assertEquals(new BigDecimal("100000.00"), totalPrincipal.setScale(2, RoundingMode.HALF_UP));
    }
}
