package com.slotsync.backend.event;

import com.slotsync.backend.event.BookingCancelledEvent;
import com.slotsync.backend.event.BookingConfirmedEvent;
import com.slotsync.backend.event.WaitlistPromotedEvent;
import com.slotsync.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        notificationService.sendNotification(
                event.getBooking().getCustomer(),
                com.slotsync.backend.domain.enums.NotificationType.BOOKING_CONFIRMED,
                "Booking Confirmed",
                "Your booking for slot " + event.getBooking().getSlot().getId() + " is confirmed."
        );
    }

    @Async
    @EventListener
    public void handleBookingCancelled(BookingCancelledEvent event) {
        notificationService.sendNotification(
                event.getBooking().getCustomer(),
                com.slotsync.backend.domain.enums.NotificationType.BOOKING_CANCELLED,
                "Booking Cancelled",
                "Your booking for slot " + event.getBooking().getSlot().getId() + " has been cancelled."
        );
    }

    @Async
    @EventListener
    public void handleWaitlistPromoted(WaitlistPromotedEvent event) {
        notificationService.sendNotification(
                event.getWaitlist().getCustomer(),
                com.slotsync.backend.domain.enums.NotificationType.WAITLIST_PROMOTED,
                "Waitlist Promotion",
                "Great news! You have been promoted from the waitlist."
        );
    }
}
