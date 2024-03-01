package com.example.bookingtechtest.response;

import com.example.bookingtechtest.dto.PropertyDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {
    private UUID id;
    private String startDate;
    private String endDate;
    private String status;
    private String guestName;
    private String guestLast4Ssn;
    private PropertyDTO property;
}
