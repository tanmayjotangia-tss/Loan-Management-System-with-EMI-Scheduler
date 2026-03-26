package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.response.EmiResponse;
import com.loanmanagementsystem.app.entity.Emi;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmiMapper {

    @Mapping(target = "loanId", source = "loan.id")
    EmiResponse toResponse(Emi emi);

    List<EmiResponse> toResponseList(List<Emi> emis);
}
