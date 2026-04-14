package com.financialapp.notifications.mapper;

import com.financialapp.notifications.model.dto.response.NotificationResponse;
import com.financialapp.notifications.model.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "type", expression = "java(notification.getType().name())")
    @Mapping(target = "channel", expression = "java(notification.getChannel().name())")
    NotificationResponse toResponse(Notification notification);
}
