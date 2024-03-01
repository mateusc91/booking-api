package com.example.bookingtechtest.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NotNull
public class CreateBookingRequest {
    @NotNull
    private UUID guestId;
    @NotNull
    private UUID propertyId;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private String guestName;
    @NotNull
    private String guestLast4Ssn;
}
