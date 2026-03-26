package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.response.NotificationResponse;
import com.loanmanagementsystem.app.entity.Notification;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "userId", source = "user.id")
    NotificationResponse toResponse(Notification notification);

    List<NotificationResponse> toResponseList(List<Notification> notifications);
}
