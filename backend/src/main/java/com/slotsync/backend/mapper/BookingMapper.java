package com.slotsync.backend.mapper;

import com.slotsync.backend.domain.Booking;
import com.slotsync.backend.dto.response.BookingResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SlotMapper.class, UserMapper.class})
public interface BookingMapper {
    BookingResponse toResponse(Booking booking);
}
