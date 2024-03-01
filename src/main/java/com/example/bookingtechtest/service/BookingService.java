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
                          ModelMapper modelMapper, PropertyRepository propertyRepository,PropertyAvailabilityValidator propertyAvailabilityValidator) {
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
        this.propertyAvailabilityValidator = propertyAvailabilityValidator;
        this.propertyRepository = propertyRepository;
    }

    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        log.info("Attempt to create booking: {}", request);

//        Guest guest = guestRepository.findById(request.getGuestId())
//                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + request.getGuestId()));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.getPropertyId()));

        // Create a new booking
        Booking booking = new Booking();
        //booking.setGuest(guest);
        booking.setGuestName(request.getGuestName());
        booking.setGuestLast4Ssn(request.getGuestLast4Ssn());
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setStatus(BookingStatus.BOOKING_CREATED.getName());
        booking.setCreated_at(LocalDateTime.now());
        //property.setAvailable(false);
        booking.setProperty(property);

        propertyAvailabilityValidator.validatePropertyAvailability(request.getStartDate(),request.getEndDate(), property);

        bookingRepository.save(booking);
        log.info("Booking successfully completed: {}", request);
        return modelMapper.map(booking,CreateBookingResponse.class);
    }

    public BookingDTO updateBooking(UUID id, UpdateBookingRequest updatedBooking) {
        log.info("Attempting to update booking : {}", updatedBooking);
        // Logic to check if the booking with the given id exists
        BookingDTO existingBooking = getBooking(id);
        Property property = modelMapper.map(existingBooking.getProperty(), Property.class);
        Booking booking = modelMapper.map(existingBooking, Booking.class);

        if(existingBooking.getStatus().equals(BookingStatus.BOOKING_CANCELED.getName())){
            log.error("This booking is canceled and cannot be updated: {}", id);
            throw new IllegalArgumentException("This booking is canceled and cannot be updated");
        }

        boolean isPropertyAvailable = propertyAvailabilityValidator.validatePropertyAvailability(updatedBooking.getStartDate(), updatedBooking.getEndDate(), property);

//        Guest updatedGuest = guestRepository.findById(updatedBooking.getGuest().getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + updatedBooking.getGuest().getId()));

        // Update the existing booking details with the new details
        if(isPropertyAvailable){
            existingBooking.setStartDate(updatedBooking.getStartDate());
            existingBooking.setEndDate(updatedBooking.getEndDate());
            existingBooking.setGuestName(updatedBooking.getGuestName());
            existingBooking.setGuestLast4Ssn(updatedBooking.getGuestLast4Ssn());
            //existingBooking.setGuest(modelMapper.map(updatedGuest,GuestDTO.class));
            existingBooking.setLast_updated_at(LocalDateTime.now());
        }

        // Save the updated booking
        bookingRepository.save(modelMapper.map(existingBooking,Booking.class));
        log.info("Booking successfully updated: {}", updatedBooking);

        return modelMapper.map(existingBooking,BookingDTO.class);
    }

    public void cancelBooking(UUID id) {
        log.info("Attempting to cancel booking : {}", id);
        // Logic to check if the booking with the given id exists
        BookingDTO existingBooking = getBooking(id);

        Property property = propertyRepository.findById(existingBooking.getProperty().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + existingBooking.getProperty().getId()));

        //property.setAvailable(true);
//         Mark the booking as cancelled
        existingBooking.setStatus(BookingStatus.BOOKING_CANCELED.getName());
        existingBooking.setLast_updated_at(LocalDateTime.now());
        existingBooking.setProperty(modelMapper.map(existingBooking.getProperty(), PropertyDTO.class));

        // Save the updated booking
        bookingRepository.save(modelMapper.map(existingBooking, Booking.class));
        log.info("Booking successfully canceled: {}", id);
    }

    public void rebookCancelledBooking(UUID id) {
        log.info("Attempting to rebook booking : {}", id);
        BookingDTO existingBookingToBeUpdated = getBooking(id);

        if(!existingBookingToBeUpdated.getStatus().equals(BookingStatus.BOOKING_CANCELED.getName())){
            log.error("The booking needs to be canceled first: {}", id);
            throw new IllegalArgumentException("The booking needs to have status canceled to be rebooked.");
        }

        UpdateBookingRequest newBooking = new UpdateBookingRequest();
        newBooking.setEndDate(existingBookingToBeUpdated.getEndDate());
        newBooking.setStartDate(existingBookingToBeUpdated.getStartDate());
        newBooking.setGuestName(existingBookingToBeUpdated.getGuestName());
        newBooking.setGuestLast4Ssn(existingBookingToBeUpdated.getGuestLast4Ssn());
        //newBooking.setGuestId(existingBooking.getGuest().getId());
        //newBooking.setPropertyId(existingBookingToBeUpdated.getProperty().getId());
//        newBooking.setS

        //bookingRepository.save(modelMapper.map(existingBooking, Booking.class));
        updateBooking(id,newBooking);
    }

    public void deleteBooking(UUID id) {
        BookingDTO existingBooking = getBooking(id);
        bookingRepository.delete(modelMapper.map(existingBooking, Booking.class));
    }

    public BookingDTO getBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        bookingDTO.setGuestName(booking.getGuestName());
        bookingDTO.setGuestLast4Ssn(booking.getGuestLast4Ssn());
        //bookingDTO.setGuest(modelMapper.map(booking.getGuest(), GuestDTO.class));
        bookingDTO.setProperty(modelMapper.map(booking.getProperty(), PropertyDTO.class));

        return bookingDTO;
    }

//    private boolean validatePropertyAvailability(LocalDate startDate, LocalDate endDate, Property property, Booking booking) {
//        boolean propertyAvailable = true;
//        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookingsForProperty(property, startDate, endDate);
//        List<Block> blocks = blockRepository.findOverlappingBlocksForProperty(property,startDate,endDate);
//        if(!overlappingBookings.isEmpty() || !blocks.isEmpty()){
//            propertyAvailable = false;
//            log.error("Failed to create booking. This property is already booked from: {} until {}", startDate , endDate);
//            throw new OverlapedBookingException("This property is not available for booking on the dates selected.");
//        }
//        return propertyAvailable;
//    }
}


