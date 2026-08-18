package com.slotsync.backend.mapper;

import com.slotsync.backend.domain.Notification;
import com.slotsync.backend.dto.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "isRead", source = "read")
    NotificationResponse toResponse(Notification notification);
}
