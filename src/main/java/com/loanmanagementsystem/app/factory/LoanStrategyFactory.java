package com.loanmanagementsystem.app.factory;

import com.loanmanagementsystem.app.entity.enums.StrategyType;
import com.loanmanagementsystem.app.stategy.FlatRate;
import com.loanmanagementsystem.app.stategy.LoanStrategy;
import com.loanmanagementsystem.app.stategy.ReducingBalance;
import com.loanmanagementsystem.app.stategy.StepUp;
import org.springframework.stereotype.Component;

@Component
public class LoanStrategyFactory {

    public LoanStrategy getStrategy(StrategyType type) {
        return switch (type) {
            case FLAT_RATE_LOAN -> new FlatRate();
            case REDUCING_BALANCE_LOAN -> new ReducingBalance();
            case STEP_UP_EMI_LOAN -> new StepUp();
        };
    }
}