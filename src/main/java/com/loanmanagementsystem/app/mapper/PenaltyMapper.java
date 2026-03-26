package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.response.PenaltyResponse;
import com.loanmanagementsystem.app.entity.Penalty;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PenaltyMapper {

    @Mapping(target = "emiId", source = "emi.id")
    @Mapping(target = "loanId", source = "loan.id")
    PenaltyResponse toResponse(Penalty penalty);

    List<PenaltyResponse> toResponseList(List<Penalty> penalties);
}
