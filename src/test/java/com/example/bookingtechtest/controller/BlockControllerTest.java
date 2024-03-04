package com.example.bookingtechtest.controller;

import com.example.bookingtechtest.dto.BlockDTO;
import com.example.bookingtechtest.entity.Block;
import com.example.bookingtechtest.entity.Property;
import com.example.bookingtechtest.repository.BlockRepository;
import com.example.bookingtechtest.request.CreateBookingRequest;
import com.example.bookingtechtest.request.UpdateBookingRequest;
import com.example.bookingtechtest.response.CreateBookingResponse;
import com.example.bookingtechtest.service.BlockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlockController.class)
class BlockControllerTest {

    @Mock
    private ModelMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlockService blockService;

    @Mock
    private BlockRepository blockRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BlockController(blockService)).build();
    }

    @Test
    @DisplayName("Create a new block")
    void given_ValidRequest_then_createBlock() throws Exception {
        Property property = new Property();
        property.setOwnerName("John Kennery");
        property.setId(UUID.fromString("e08c2cf5-7a6b-4788-ad0f-251f5a4a93fc"));
        String startDate = "2024-02-01";
        String endDate = "2024-02-05";
        LocalDate startDateParsed = LocalDate.parse(startDate);
        LocalDate endDateParsed = LocalDate.parse(endDate);
        Block block = new Block();
        UUID blockId = UUID.randomUUID();
        block.setId(blockId);
        block.setStartDate(startDateParsed);
        block.setEndDate(endDateParsed);
        block.setProperty(property);
        mapper.map(block, CreateBookingRequest.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/blocks/create-block")
                        .content(objectMapper.writeValueAsString(block))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Update an existing block")
    void given_ValidRequest_then_updateBlock() throws Exception {
        // Define test data
        UUID blockId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");
        String newStartDate = "2024-03-01";
        String newEndDate = "2024-03-05";
        LocalDate newStartDateParsed = LocalDate.parse(newStartDate);
        LocalDate newEndDateParsed = LocalDate.parse(newEndDate);

        // Create an updated block with the same ID as the existing block
        Block updatedBlock = new Block();
        updatedBlock.setId(blockId);
        updatedBlock.setStartDate(newStartDateParsed);
        updatedBlock.setEndDate(newEndDateParsed);

        // Mock the behavior of findById() to return an existing block
        Block existingBlock = new Block();
        existingBlock.setId(blockId);
        existingBlock.setStartDate(LocalDate.parse("2024-02-01"));
        existingBlock.setEndDate(LocalDate.parse("2024-02-05"));
        when(blockRepository.findById(blockId)).thenReturn(Optional.of(existingBlock));

        // Perform the update request using MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/blocks/update-block/{id}", blockId)
                        .content(objectMapper.writeValueAsString(updatedBlock))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Delete an existing block")
    void given_ValidRequest_then_deleteBlock() throws Exception {
        // Define test data
        UUID blockId = UUID.fromString("3b3e5980-b136-42b2-ab00-024948232e96");

        // Mock the behavior of findById() to return an existing block
        Block existingBlock = new Block();
        existingBlock.setId(blockId);
        when(blockRepository.findById(blockId)).thenReturn(Optional.of(existingBlock));

        // Perform the delete request using MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/blocks/delete-block/{id}", blockId))
                .andExpect(status().isNoContent());
    }

}
