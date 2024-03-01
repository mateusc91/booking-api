package com.example.bookingtechtest.dto;

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
public class BlockDTO {
    private UUID id;
    private String startDate;
    private String endDate;
    private PropertyDTO property;
}
