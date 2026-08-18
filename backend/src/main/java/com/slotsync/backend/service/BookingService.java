package com.slotsync.backend.service;

import com.slotsync.backend.dto.request.BookingRequest;
import com.slotsync.backend.dto.response.BookingResponse;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    BookingResponse createBooking(UUID customerId, BookingRequest request);
    List<BookingResponse> getBookingsByCustomer(UUID customerId);
    void cancelBooking(UUID customerId, UUID bookingId);
}
