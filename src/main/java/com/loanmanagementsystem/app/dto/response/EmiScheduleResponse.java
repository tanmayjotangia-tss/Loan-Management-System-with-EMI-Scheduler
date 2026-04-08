package com.loanmanagementsystem.app.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiScheduleResponse {

    private Integer totalInstallments;
    private List<EmiResponse> schedule;
}
