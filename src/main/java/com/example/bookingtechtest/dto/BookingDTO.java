package com.example.bookingtechtest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String startDate;
    private String endDate;
    private String status;
    private String created_at;
    private String last_updated_at;
    private String guestName;
    private String guestLast4Ssn;
    private PropertyDTO property;


}
