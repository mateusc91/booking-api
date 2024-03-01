package com.example.bookingtechtest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "TB_PROPERTY")
public class Property {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "ID", updatable = false, nullable = false, length = 16)
    private UUID id;

    @NotNull
    private String ownerName;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<Block> block;

//    @Column(name = "available")
//    private boolean isAvailable;

    // In real life, we would implement more fields such as ownerlast4ssn,propertyDescription, etc.

}
