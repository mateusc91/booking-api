package com.example.bookingtechtest.controller;

import com.example.bookingtechtest.dto.BookingDTO;
import com.example.bookingtechtest.entity.Booking;
import com.example.bookingtechtest.request.CreateBookingRequest;
import com.example.bookingtechtest.request.UpdateBookingRequest;
import com.example.bookingtechtest.response.CreateBookingResponse;
import com.example.bookingtechtest.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@Tag(name = "Bookings", description = "The bookings API")
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(
            summary = "Creates a booking",
            description = "validates the availability of a property and creates a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "404", description = "resource not found request")
    })
    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest booking) {
        CreateBookingResponse createdBooking = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @Operation(
            summary = "Updates a booking",
            description = "validates the availability of a property and updates a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "404", description = "resource not found request")
    })
    @PutMapping("/update-booking/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable UUID id, @Valid @RequestBody UpdateBookingRequest booking) {
        BookingDTO updatedBooking = bookingService.updateBooking(id, booking);
        return ResponseEntity.ok(updatedBooking);
    }

    @Operation(
            summary = "Cancel a booking",
            description = "validates the availability of a property and cancel a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "404", description = "resource not found request")
    })
    @PutMapping("/cancel-booking/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(
            summary = "Rebooks a booking",
            description = "validates the availability of a property and rebook a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "404", description = "resource not found request")
    })
    @PutMapping("/rebook-booking/{id}")
    public ResponseEntity<Void> rebookCancelledBooking(@PathVariable UUID id) {
        bookingService.rebookCancelledBooking(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deletes a booking",
            description = "deletes an existing booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "404", description = "resource not found request")
    })
    @DeleteMapping("/delete-booking/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(
            summary = "Queries a booking",
            description = "queries an existing booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "404", description = "resource not found request")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable UUID id) {
        BookingDTO booking = bookingService.getBooking(id);
        return ResponseEntity.ok(booking);
    }
}

