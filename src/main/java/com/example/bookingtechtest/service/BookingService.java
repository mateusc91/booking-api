package com.example.bookingtechtest.service;

import com.example.bookingtechtest.dto.BookingDTO;
import com.example.bookingtechtest.dto.PropertyDTO;
import com.example.bookingtechtest.entity.Booking;
import com.example.bookingtechtest.entity.Property;
import com.example.bookingtechtest.enums.BookingStatus;
import com.example.bookingtechtest.exception.ResourceNotFoundException;
import com.example.bookingtechtest.repository.BookingRepository;
import com.example.bookingtechtest.repository.PropertyRepository;
import com.example.bookingtechtest.request.CreateBookingRequest;
import com.example.bookingtechtest.request.UpdateBookingRequest;
import com.example.bookingtechtest.response.CreateBookingResponse;
import com.example.bookingtechtest.validator.PropertyAvailabilityValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private ModelMapper modelMapper;
    private PropertyAvailabilityValidator propertyAvailabilityValidator;

    public BookingService(BookingRepository bookingRepository,
                          ModelMapper modelMapper, PropertyRepository propertyRepository,
                          PropertyAvailabilityValidator propertyAvailabilityValidator) {
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
        this.propertyAvailabilityValidator = propertyAvailabilityValidator;
        this.propertyRepository = propertyRepository;
    }

    public BookingDTO getBookingById(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        bookingDTO.setGuestName(booking.getGuestName());
        bookingDTO.setGuestLast4Ssn(booking.getGuestLast4Ssn());
        bookingDTO.setProperty(modelMapper.map(booking.getProperty(), PropertyDTO.class));

        return bookingDTO;
    }

    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        log.info("Attempt to create booking: {}", request);

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> {
                    log.error("An error occurred. Booking can't be created because the property was not found with id: {}", request.getPropertyId());
                    return new ResourceNotFoundException("Property not found with id: " + request.getPropertyId());
                });

        Booking booking = new Booking();
        booking.setGuestName(request.getGuestName());
        booking.setGuestLast4Ssn(request.getGuestLast4Ssn());
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setStatus(BookingStatus.BOOKING_CREATED.getName());
        booking.setCreated_at(LocalDateTime.now());
        booking.setProperty(property);

        propertyAvailabilityValidator.validatePropertyAvailability(request.getStartDate(),request.getEndDate(), property);

        bookingRepository.save(booking);
        log.info("Booking successfully completed: {}", request);
        return modelMapper.map(booking,CreateBookingResponse.class);
    }

    public BookingDTO updateBooking(UUID id, UpdateBookingRequest updatedBooking) {
        log.info("Attempting to update booking : {}", updatedBooking);
        Booking existingBooking = getBooking(id);
        Property property = existingBooking.getProperty();

        if(existingBooking.getStatus().equals(BookingStatus.BOOKING_CANCELED.getName())){
            log.error("This booking is canceled and cannot be updated: {}", id);
            throw new IllegalArgumentException("This booking is canceled and cannot be updated");
        }

        boolean isPropertyAvailable = propertyAvailabilityValidator.validatePropertyAvailability(updatedBooking.getStartDate(), updatedBooking.getEndDate(), property);

        // Update the existing booking details with the new details
        if(isPropertyAvailable){
            existingBooking.setStartDate(updatedBooking.getStartDate());
            existingBooking.setEndDate(updatedBooking.getEndDate());
            existingBooking.setGuestName(updatedBooking.getGuestName());
            existingBooking.setGuestLast4Ssn(updatedBooking.getGuestLast4Ssn());
            existingBooking.setLast_updated_at(LocalDateTime.now());
        }

        bookingRepository.save(existingBooking);
        log.info("Booking successfully updated: {}", updatedBooking);

        return modelMapper.map(existingBooking,BookingDTO.class);
    }

    public void cancelBooking(UUID id) {
        log.info("Attempting to cancel booking : {}", id);
        Booking existingBooking = getBooking(id);

        existingBooking.setStatus(BookingStatus.BOOKING_CANCELED.getName());
        existingBooking.setLast_updated_at(LocalDateTime.now());
        existingBooking.setProperty(existingBooking.getProperty());

        bookingRepository.save(existingBooking);
        log.info("Booking successfully canceled: {}", id);
    }



    public void rebookCancelledBooking(UUID id) {
        log.info("Attempting to rebook booking : {}", id);
        Booking existingBookingToBeUpdated = getBooking(id);

        if(!existingBookingToBeUpdated.getStatus().equals(BookingStatus.BOOKING_CANCELED.getName())){
            log.error("The booking needs to be canceled first: {}", id);
            throw new IllegalArgumentException("The booking needs to have status canceled to be rebooked.");
        }

        existingBookingToBeUpdated.setStatus(BookingStatus.BOOKING_REBOOKED.getName());
        bookingRepository.save(existingBookingToBeUpdated);
    }

    public void deleteBooking(UUID id) {
        Booking existingBooking = getBooking(id);
        bookingRepository.delete(existingBooking);
    }

    public Booking getBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        return booking;
    }
}


