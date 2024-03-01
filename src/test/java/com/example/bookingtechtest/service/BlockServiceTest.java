package com.example.bookingtechtest.service;

import com.example.bookingtechtest.controller.BlockController;
import com.example.bookingtechtest.dto.BlockDTO;
import com.example.bookingtechtest.entity.Block;
import com.example.bookingtechtest.entity.Property;
import com.example.bookingtechtest.exception.OverlapedBookingException;
import com.example.bookingtechtest.repository.BlockRepository;
import com.example.bookingtechtest.service.BlockService;
import com.example.bookingtechtest.validator.PropertyAvailabilityValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {

    @Mock
    private ModelMapper mapper ;

    @Mock
    private BlockService blockService;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private PropertyAvailabilityValidator propertyAvailabilityValidator;

    @BeforeEach
    void setUp() {
        blockService = new BlockService(blockRepository, mapper, propertyAvailabilityValidator);
    }

    @Test
    @DisplayName("Creating a block successfully")
    void given_ValidRequest_then_createBlock() throws Exception {
        String startDate = "2024-02-01";
        String endDate = "2024-02-05";
        LocalDate startDateParsed = LocalDate.parse(startDate);
        LocalDate endDateParsed = LocalDate.parse(endDate);
        // Prepare data
        UUID blockId = UUID.randomUUID();
        BlockDTO blockDTO = BlockDTO.builder()
                .id(blockId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Property property = new Property();
        property.setOwnerName("John Kennery");
        property.setId(UUID.fromString("e08c2cf5-7a6b-4788-ad0f-251f5a4a93fc"));

        Block block = new Block();
        block.setId(blockId);
        block.setStartDate(startDateParsed);
        block.setEndDate(endDateParsed);
        block.setProperty(property);
        when(mapper.map(block, BlockDTO.class)).thenReturn(blockDTO);

        var response = blockService.createBlock(block);
        verify(blockRepository, times(1)).save(block);
        verify(mapper, times(1)).map(block, BlockDTO.class);

        assertEquals(blockId,response.getId());
        assertEquals(startDate,response.getStartDate());
        assertEquals(endDate,response.getEndDate());
    }

    @Test
    @DisplayName("Attempt to create a block and throwing OverlapedBookingException due date availability ")
    void given_InvalidRequest_then_throwOverlapedBookingException() throws Exception {
        String startDate = "2024-02-01";
        String endDate = "2024-02-05";
        LocalDate startDateParsed = LocalDate.parse(startDate);
        LocalDate endDateParsed = LocalDate.parse(endDate);

        UUID blockId = UUID.randomUUID();
        Property property = new Property();
        property.setOwnerName("John Kennery");
        property.setId(UUID.fromString("e08c2cf5-7a6b-4788-ad0f-251f5a4a93fc"));

        Block block = new Block();
        block.setId(blockId);
        block.setStartDate(startDateParsed);
        block.setEndDate(endDateParsed);
        block.setProperty(property);

        // Mock behavior of blockRepository.save(block) to throw OverlapedBookingException
        doThrow(new OverlapedBookingException("This property is already blocked for booking on the dates selected.")).when(blockRepository).save(block);

        // Perform request and verify
        assertThrows(OverlapedBookingException.class, () -> blockService.createBlock(block));
    }

}

