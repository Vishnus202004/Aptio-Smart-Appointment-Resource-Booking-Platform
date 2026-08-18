package com.slotsync.backend.mapper;

import com.slotsync.backend.domain.Slot;
import com.slotsync.backend.dto.response.SlotResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SlotMapper {

    @Mapping(target = "providerProfileId", source = "providerProfile.id")
    SlotResponse toResponse(Slot slot);
}
