package com.example.bookingtechtest.service;

import com.example.bookingtechtest.entity.Booking;
import com.example.bookingtechtest.entity.Property;
import com.example.bookingtechtest.enums.BookingStatus;
import com.example.bookingtechtest.exception.OverlapedBookingException;
import com.example.bookingtechtest.exception.ResourceNotFoundException;
import com.example.bookingtechtest.repository.BookingRepository;
import com.example.bookingtechtest.repository.PropertyRepository;
import com.example.bookingtechtest.request.CreateBookingRequest;
import com.example.bookingtechtest.request.UpdateBookingRequest;
import com.example.bookingtechtest.response.CreateBookingResponse;
import com.example.bookingtechtest.validator.PropertyAvailabilityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private ModelMapper mapper ;

    @Mock
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyAvailabilityValidator propertyAvailabilityValidator;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, mapper,propertyRepository, propertyAvailabilityValidator);
    }

    @Test
    @DisplayName("Creating a booking successfully")
    void given_ValidRequest_then_createBlock() throws Exception {
        // Prepare data
        String startDate = "2024-02-01";
        String endDate = "2024-02-05";
        LocalDate startDateParsed = LocalDate.parse(startDate);
        LocalDate endDateParsed = LocalDate.parse(endDate);

        // Prepare data
        UUID bookingId = UUID.randomUUID();
        UUID propertyId = UUID.fromString("e08c2cf5-7a6b-4788-ad0f-251f5a4a93fc");

        Property property = new Property();
        property.setOwnerName("John Kennery");
        property.setId(propertyId);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStartDate(startDateParsed);
        booking.setEndDate(endDateParsed);
        booking.setStatus("Booked");
        booking.setGuestName("Neymar Junior");
        booking.setGuestLast4Ssn("01234");
        booking.setProperty(property);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setEndDate(endDateParsed);
        request.setStartDate(startDateParsed);
        request.setGuestName("Neymar Junior");
        request.setGuestLast4Ssn("01234");
        request.setPropertyId(propertyId);

        // Mock behavior of propertyRepository.findById() to return the property object
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // Capture the Booking object passed to bookingRepository.save
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        // Perform request
        bookingService.createBooking(request);

        // Verify that the bookingRepository's save method is called with a Booking object
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking capturedBooking = bookingCaptor.getValue();

        // Verify that the captured Booking object matches the expected values
        assertEquals(startDateParsed, capturedBooking.getStartDate());
        assertEquals(endDateParsed, capturedBooking.getEndDate());
        assertEquals("Booked", capturedBooking.getStatus());
        assertEquals("Neymar Junior", capturedBooking.getGuestName());
        assertEquals("01234", capturedBooking.getGuestLast4Ssn());
        assertEquals(property, capturedBooking.getProperty());
    }


    @Test
    @DisplayName("Attempt to create a booking and throwing OverlapedBookingException due date availability")
    void given_InvalidRequest_then_throwOverlapedBookingException() throws Exception {
        // Define test data
        String startDate = "2024-02-01";
        String endDate = "2024-02-05";
        LocalDate startDateParsed = LocalDate.parse(startDate);
        LocalDate endDateParsed = LocalDate.parse(endDate);
        UUID propertyId = UUID.fromString("e08c2cf5-7a6b-4788-ad0f-251f5a4a93fc");

        // Mock the behavior of propertyRepository.findById() to return a property
        Property property = new Property();
        property.setId(propertyId);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // Mock the behavior of propertyAvailabilityValidator.validatePropertyAvailability() to throw OverlapedBookingException
        doThrow(new OverlapedBookingException("Validation failed")).when(propertyAvailabilityValidator)
                .validatePropertyAvailability(startDateParsed, endDateParsed, property);

        // Create a booking request
        CreateBookingRequest request = new CreateBookingRequest();
        request.setStartDate(startDateParsed);
        request.setEndDate(endDateParsed);
        request.setGuestName("John Doe");
        request.setGuestLast4Ssn("1234");
        request.setPropertyId(propertyId);

        // Attempt to create a booking and expect OverlapedBookingException to be thrown
        assertThrows(OverlapedBookingException.class, () -> {
            bookingService.createBooking(request);
        });

        // Verify that propertyRepository.findById() is called with the correct propertyId
        verify(propertyRepository, times(1)).findById(propertyId);

        // Verify that propertyAvailabilityValidator.validatePropertyAvailability() is called with the correct arguments
        verify(propertyAvailabilityValidator, times(1))
                .validatePropertyAvailability(startDateParsed, endDateParsed, property);

        // Ensure that bookingRepository.save() is not called
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Updating a booking successfully")
    void given_ValidRequest_then_updateBooking() {
        // Prepare data
        UUID bookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");
        String startDate = "2024-02-01";
        String endDate = "2024-02-05";
        String guestName = "John Doe";
        String guestLast4Ssn = "1234";
        LocalDate startDateParsed = LocalDate.parse(startDate);
        LocalDate endDateParsed = LocalDate.parse(endDate);

        // Create an updated booking request
        UpdateBookingRequest updatedBooking = new UpdateBookingRequest();
        updatedBooking.setStartDate(startDateParsed);
        updatedBooking.setEndDate(endDateParsed);
        updatedBooking.setGuestName(guestName);
        updatedBooking.setGuestLast4Ssn(guestLast4Ssn);
        updatedBooking.setBookingStatus(BookingStatus.BOOKING_CREATED.getName());

        // Mock the existing booking DTO returned by the service
        Booking existingBookingDTO = new Booking();
        existingBookingDTO.setId(bookingId);
        existingBookingDTO.setStartDate(startDateParsed);
        existingBookingDTO.setEndDate(endDateParsed); // Assuming an initial end date
        existingBookingDTO.setGuestName("Previous Guest");
        existingBookingDTO.setGuestLast4Ssn("5678");
        existingBookingDTO.setProperty(new Property()); // Set a dummy property DTO
        existingBookingDTO.setStatus(BookingStatus.BOOKING_CREATED.getName());

        // Mock the behavior of bookingRepository.findById() to return an existing booking
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBookingDTO));

        // Call the service method
        bookingService.updateBooking(bookingId, updatedBooking);

        // Verify that the repository's save method is called with the updated booking entity
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Attempt to update a canceled booking")
    void given_CanceledBooking_then_throwException_on_updateBooking() {
        // Define test data
        UUID bookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");
        UpdateBookingRequest updateRequest = new UpdateBookingRequest();

        // Mock the behavior of getBooking() to return a canceled booking
        Booking canceledBooking = new Booking();
        canceledBooking.setStatus(BookingStatus.BOOKING_CANCELED.getName());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(canceledBooking));

        // Call the updateBooking() method and expect an IllegalArgumentException to be thrown
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.updateBooking(bookingId, updateRequest);
        });

        // Ensure that bookingRepository.save() is not called
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Delete an existing booking")
    void given_ValidRequest_then_deleteBooking2() {
        // Define test data
        UUID bookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");

        // Mock the behavior of bookingRepository.findById() to return an existing booking
        Booking existingBooking = new Booking();
        existingBooking.setId(bookingId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));

        // Call the deleteBooking() method
        bookingService.deleteBooking(bookingId);

        // Verify that the deleted booking is removed from the repository
        verify(bookingRepository, times(1)).delete(existingBooking);
    }

    @Test
    @DisplayName("Cancel a non-existing booking")
    void given_NonExistingBooking_then_throwResourceNotFoundException() {
        // Define test data
        UUID nonExistingBookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e97");

        // Mock the behavior of bookingRepository.findById() to return an empty Optional
        when(bookingRepository.findById(nonExistingBookingId)).thenReturn(Optional.empty());

        // Call the cancelBooking() method and expect a ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> bookingService.cancelBooking(nonExistingBookingId));

        // Verify that the bookingRepository.findById() method is called with the correct ID
        verify(bookingRepository, times(1)).findById(nonExistingBookingId);
    }

    @Test
    @DisplayName("Attempt to rebook a non-canceled booking")
    void given_NonCanceledBooking_then_throwException_on_rebookCancelledBooking() {
        // Define test data
        UUID bookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");

        // Mock the behavior of getBooking() to return a non-canceled booking
        Booking nonCanceledBooking = new Booking();
        nonCanceledBooking.setStatus(BookingStatus.BOOKING_CREATED.getName());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(nonCanceledBooking));

        // Call the rebookCancelledBooking() method and expect an IllegalArgumentException to be thrown
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.rebookCancelledBooking(bookingId);
        });

        // Ensure that bookingRepository.save() is not called
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Rebook a canceled booking")
    void given_CanceledBooking_then_rebookCancelledBooking() {
        // Define test data
        UUID bookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");

        // Mock the behavior of getBooking() to return a canceled booking
        Booking canceledBooking = new Booking();
        canceledBooking.setStatus(BookingStatus.BOOKING_CANCELED.getName());

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(canceledBooking));

        // Call the rebookCancelledBooking() method
        bookingService.rebookCancelledBooking(bookingId);

        // Verify that the rebooked booking is saved
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Delete an existing booking")
    void given_ValidRequest_then_deleteBooking() {
        // Define test data
        UUID bookingId = UUID.randomUUID();

        // Mock the behavior of bookingRepository.findById() to return an existing booking
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(new Booking()));

        // Call the deleteBooking() method
        bookingService.deleteBooking(bookingId);

        // Verify that the deleted booking is removed from the repository
        verify(bookingRepository, times(1)).delete(any(Booking.class));
    }

}

