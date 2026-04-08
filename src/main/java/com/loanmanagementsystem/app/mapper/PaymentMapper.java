package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.request.PaymentRequest;
import com.loanmanagementsystem.app.dto.response.PaymentResponse;
import com.loanmanagementsystem.app.entity.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "loan", ignore = true)
    @Mapping(target = "emi", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    Payment toEntity(PaymentRequest request);

    @Mapping(target = "loanId", source = "loan.id")
    @Mapping(target = "installmentNumber", source = "emi.installmentNumber")
    PaymentResponse toResponse(Payment payment);
}
