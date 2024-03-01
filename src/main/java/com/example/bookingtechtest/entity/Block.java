package com.example.bookingtechtest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "TB_BLOCK")
public class Block {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "ID", updatable = false, nullable = false, length = 16)
    private UUID id;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime created_at ;

    private LocalDateTime last_updated_at;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id")
    private Property property;
}

