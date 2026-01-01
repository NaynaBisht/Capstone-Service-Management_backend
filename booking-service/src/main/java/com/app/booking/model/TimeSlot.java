package com.app.booking.model;

import java.time.LocalTime;

public enum TimeSlot {

    SLOT_9_11(LocalTime.of(9, 0), LocalTime.of(11, 0)),
    SLOT_11_13(LocalTime.of(11, 0), LocalTime.of(13, 0));

    private final LocalTime start;
    private final LocalTime end;

    TimeSlot(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }
}
