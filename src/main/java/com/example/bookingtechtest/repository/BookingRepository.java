package com.example.bookingtechtest.repository;

import com.example.bookingtechtest.entity.Booking;
import com.example.bookingtechtest.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("SELECT b FROM Booking b " +
            "WHERE b.property = :property " +
            "AND (:startDate BETWEEN b.startDate AND b.endDate " +
            "OR :endDate BETWEEN b.startDate AND b.endDate " +
            "OR b.startDate BETWEEN :startDate AND :endDate " +
            "OR b.endDate BETWEEN :startDate AND :endDate)")
    List<Booking> findOverlappingBookingsForProperty(
            @Param("property") Property property,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
