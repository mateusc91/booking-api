package com.example.bookingtechtest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime last_updated_at;
    //private GuestDTO guest;
    private String guestName;
    private String guestLast4Ssn;
    private PropertyDTO property;


}
