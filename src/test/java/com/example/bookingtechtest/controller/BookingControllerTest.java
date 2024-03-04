package com.example.bookingtechtest.controller;


import com.example.bookingtechtest.entity.Booking;
import com.example.bookingtechtest.entity.Property;
import com.example.bookingtechtest.enums.BookingStatus;
import com.example.bookingtechtest.repository.BookingRepository;
import com.example.bookingtechtest.repository.PropertyRepository;
import com.example.bookingtechtest.request.CreateBookingRequest;
import com.example.bookingtechtest.request.UpdateBookingRequest;
import com.example.bookingtechtest.service.BookingService;
import com.example.bookingtechtest.validator.PropertyAvailabilityValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Mock
    private ModelMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PropertyAvailabilityValidator propertyAvailabilityValidator;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BookingController(bookingService)).build();
    }

    @Test
    @DisplayName("Creating a booking successfully")
    void given_ValidRequest_then_createBooking() throws Exception {
        // Define test data
        UUID propertyId = UUID.fromString("e08c2cf5-7a6b-4788-ad0f-251f5a4a93fc");
        String startDate = "2024-02-01";
        String endDate = "2024-02-05";
        LocalDate startDateParsed = LocalDate.parse(startDate);
        LocalDate endDateParsed = LocalDate.parse(endDate);

        // Prepare request body
        CreateBookingRequest request = new CreateBookingRequest();
        request.setStartDate(startDateParsed);
        request.setEndDate(endDateParsed);
        request.setGuestName("Neymar Junior");
        request.setGuestLast4Ssn("01234");
        request.setPropertyId(propertyId);

        // Mock the behavior of propertyRepository.findById() to return a property
        Property property = new Property();
        property.setId(propertyId);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // Perform the create booking request using MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/bookings/create-booking")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Updating a booking successfully")
    void given_ValidRequest_then_updateBooking() throws Exception {
        // Define test data
        UUID bookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");
        String newStartDate = "2024-03-01";
        String newEndDate = "2024-03-05";
        LocalDate newStartDateParsed = LocalDate.parse(newStartDate);
        LocalDate newEndDateParsed = LocalDate.parse(newEndDate);

        // Prepare request body
        UpdateBookingRequest request = new UpdateBookingRequest();
        request.setStartDate(newStartDateParsed);
        request.setEndDate(newEndDateParsed);
        request.setGuestName("Neymar Junior");
        request.setGuestLast4Ssn("0123543543");

        // Mock the behavior of getBooking() to return an existing booking
        Booking existingBooking = new Booking();
        existingBooking.setId(bookingId);
        when(bookingService.getBooking(bookingId)).thenReturn(existingBooking);

        // Perform the update booking request using MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/bookings/update-booking/" + bookingId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Rebook a canceled booking")
    void given_CanceledBooking_then_rebookBooking() throws Exception {
        // Define test data
        UUID bookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");

        // Mock the behavior of getBooking() to return a canceled booking
        Booking canceledBooking = new Booking();
        canceledBooking.setId(bookingId);
        canceledBooking.setStatus(BookingStatus.BOOKING_CANCELED.getName());
        when(bookingService.getBooking(bookingId)).thenReturn(canceledBooking);

        // Perform the rebook request using MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/bookings/rebook-booking/{id}", bookingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete an existing booking")
    void given_ExistingBooking_then_deleteBooking() throws Exception {
        // Define test data
        UUID bookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");

        // Mock the behavior of getBooking() to return an existing booking
        Booking existingBooking = new Booking();
        existingBooking.setId(bookingId);
        when(bookingService.getBooking(bookingId)).thenReturn(existingBooking);

        // Perform the delete request using MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/bookings/delete-booking/{id}", bookingId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Attempt to update a canceled booking")
    void given_CanceledBooking_then_throwIllegalStateException() throws Exception {
        // Define test data
        UUID bookingId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");

        // Mock the behavior of bookingRepository.findById() to return a canceled booking
        Booking canceledBooking = new Booking();
        canceledBooking.setStatus(BookingStatus.BOOKING_CANCELED.getName());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(canceledBooking));

        // Prepare request data
        UpdateBookingRequest request = new UpdateBookingRequest();
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(3));

        // Perform request and verify
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/bookings/cancel-booking/" + bookingId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }





}
