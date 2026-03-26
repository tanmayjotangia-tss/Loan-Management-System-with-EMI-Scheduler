package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.PenaltyReason;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PenaltyResponse {

    private Long id;
    private Long emiId;
    private Long loanId;
    private BigDecimal amount;
    private PenaltyReason reason;
    private LocalDate appliedDate;
    private Boolean isPaid;
    private LocalDate paidDate;
}
