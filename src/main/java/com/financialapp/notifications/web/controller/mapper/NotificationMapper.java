package com.financialapp.notifications.web.controller.mapper;

import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.domain.model.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "type", expression = "java(notification.type().name())")
    @Mapping(target = "channel", expression = "java(notification.channel().name())")
    NotificationResponse toResponse(Notification notification);
}
