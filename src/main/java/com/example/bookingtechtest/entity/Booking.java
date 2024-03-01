package com.example.bookingtechtest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "TB_BOOKINGS")
public class Booking {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "ID", updatable = false, nullable = false, length = 16)
    private UUID id;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String status;

    private LocalDateTime created_at ;

    private LocalDateTime last_updated_at;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "guest_id")
//    private Guest guest;

    private String guestName;

    private String guestLast4Ssn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id")
    private Property property;
}
