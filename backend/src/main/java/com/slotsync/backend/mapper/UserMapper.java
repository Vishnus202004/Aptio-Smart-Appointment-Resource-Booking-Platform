package com.slotsync.backend.mapper;

import com.slotsync.backend.domain.User;
import com.slotsync.backend.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
