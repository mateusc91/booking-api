package com.example.bookingtechtest.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Audited
@Table(name = "TB_BOOKINGS")
public class Booking {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "ID", updatable = false, nullable = false, length = 16)
    private UUID id;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private LocalDateTime created_at ;

    private LocalDateTime last_updated_at;

    private String guestName;

    private String guestLast4Ssn;

    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id")
    private Property property;
}
