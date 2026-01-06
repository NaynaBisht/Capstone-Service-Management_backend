package com.app.booking.model;

import java.time.LocalTime;

public enum TimeSlot {

    // Morning Slots
    SLOT_9_11(LocalTime.of(9, 0), LocalTime.of(11, 0)),
    SLOT_11_13(LocalTime.of(11, 0), LocalTime.of(13, 0)), // 13:00 is 1 PM

    // Afternoon Slots
    SLOT_14_16(LocalTime.of(14, 0), LocalTime.of(16, 0)), // 14:00 is 2 PM
    SLOT_16_18(LocalTime.of(16, 0), LocalTime.of(18, 0)); // 16:00 is 4 PM, 18:00 is 6 PM

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