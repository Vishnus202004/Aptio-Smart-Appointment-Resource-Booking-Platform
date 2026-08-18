package com.slotsync.backend.event;

import com.slotsync.backend.domain.Waitlist;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WaitlistPromotedEvent extends ApplicationEvent {
    private final Waitlist waitlist;

    public WaitlistPromotedEvent(Object source, Waitlist waitlist) {
        super(source);
        this.waitlist = waitlist;
    }
}
