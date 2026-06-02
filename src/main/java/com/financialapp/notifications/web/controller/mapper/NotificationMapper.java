package com.financialapp.notifications.web.controller.mapper;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.web.controller.dto.response.NotificationResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "type", expression = "java(notification.type().name())")
    @Mapping(target = "channel", expression = "java(notification.channel().name())")
    NotificationResponse toResponse(Notification notification);
}
