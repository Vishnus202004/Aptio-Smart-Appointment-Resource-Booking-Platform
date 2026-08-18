package com.slotsync.backend.event;

import com.slotsync.backend.domain.Booking;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookingConfirmedEvent extends ApplicationEvent {
    private final Booking booking;

    public BookingConfirmedEvent(Object source, Booking booking) {
        super(source);
        this.booking = booking;
    }
}
