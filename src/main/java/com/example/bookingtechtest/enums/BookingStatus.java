package com.example.bookingtechtest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {
    BOOKING_CREATED(1, "Booked"),
    BOOKING_CANCELED(2, "Canceled"),
    BOOKING_UPDATED(3, "Updated"),
    BOOKING_REBOOKED(4, "Rebooked");

    private Integer id;

    private String name;
}
