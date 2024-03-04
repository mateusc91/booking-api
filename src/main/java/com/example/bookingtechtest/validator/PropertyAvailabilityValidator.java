package com.example.bookingtechtest.validator;

import com.example.bookingtechtest.entity.Block;
import com.example.bookingtechtest.entity.Booking;
import com.example.bookingtechtest.entity.Property;
import com.example.bookingtechtest.exception.OverlapedBookingException;
import com.example.bookingtechtest.repository.BlockRepository;
import com.example.bookingtechtest.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class PropertyAvailabilityValidator {
    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;

    public PropertyAvailabilityValidator(BookingRepository bookingRepository, BlockRepository blockRepository) {
        this.bookingRepository = bookingRepository;
        this.blockRepository = blockRepository;
    }

    public boolean validatePropertyAvailability(LocalDate startDate, LocalDate endDate, Property property) {
        boolean propertyAvailable = true;
        if(startDate.isAfter(endDate)){
            log.error("The startDate must be earlier than the endDate");
            throw new IllegalArgumentException("The start date cannot be after the end date. Please check the dates entered");

        }
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookingsForProperty(property, startDate, endDate);
        List<Block> blocks = blockRepository.findOverlappingBlocksForProperty(property,startDate,endDate);
        if(!overlappingBookings.isEmpty() || !blocks.isEmpty()){
            propertyAvailable = false;
            log.error("Failed to create booking. This property is already booked from: {} until {}", startDate , endDate);
            throw new OverlapedBookingException("This property is not available for booking on the dates selected.");
        }
        return propertyAvailable;
    }

    public boolean validateBlockPropertyAvailability(LocalDate startDate, LocalDate endDate, Property property) {
        boolean propertyAvailable = true;

        if(startDate.isAfter(endDate)){
            log.error("The startDate must be earlier than the endDate");
            throw new IllegalArgumentException("The start date cannot be after the end date. Please check the dates entered");

        }
        List<Block> blocks = blockRepository.findOverlappingBlocksForProperty(property,startDate,endDate);
        if(!blocks.isEmpty()){
            propertyAvailable = false;
            log.error("Failed to create block. This property is already blocked from: {} until {}", startDate , endDate);
            throw new OverlapedBookingException("This property is already blocked for booking on the dates selected.");
        }
        return propertyAvailable;
    }
}
