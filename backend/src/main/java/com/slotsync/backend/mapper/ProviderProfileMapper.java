package com.slotsync.backend.mapper;

import com.slotsync.backend.domain.ProviderProfile;
import com.slotsync.backend.dto.response.ProviderProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProviderProfileMapper {

    @Mapping(target = "userId", source = "user.id")
    ProviderProfileResponse toResponse(ProviderProfile providerProfile);
}
