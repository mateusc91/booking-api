package com.example.bookingtechtest.service;

import com.example.bookingtechtest.dto.GuestDTO;
import com.example.bookingtechtest.entity.Guest;
import com.example.bookingtechtest.repository.GuestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class GuestService {
    private final GuestRepository guestRepository;
    private ModelMapper modelMapper;

    public GuestService(GuestRepository guestRepository,ModelMapper modelMapper) {
        this.guestRepository = guestRepository;
        this.modelMapper = modelMapper;
    }

    public GuestDTO createGuest(Guest guest) {
        // Logic to save the guest in the database
        guest.setCreated_at(LocalDateTime.now());
        guestRepository.save(guest);
        return modelMapper.map(guest, GuestDTO.class);
    }
}


