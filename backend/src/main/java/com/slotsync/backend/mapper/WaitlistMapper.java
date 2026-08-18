package com.slotsync.backend.mapper;

import com.slotsync.backend.domain.Waitlist;
import com.slotsync.backend.dto.response.WaitlistResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WaitlistMapper {

    @Mapping(target = "slotId", source = "slot.id")
    @Mapping(target = "customerId", source = "customer.id")
    WaitlistResponse toResponse(Waitlist waitlist);
}
