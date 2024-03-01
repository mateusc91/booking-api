package com.example.bookingtechtest.controller;

import com.example.bookingtechtest.dto.BlockDTO;
import com.example.bookingtechtest.entity.Block;
import com.example.bookingtechtest.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Blocks", description = "The blocks API")
@RestController
@RequestMapping("/blocks")
public class BlockController {
    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @Operation(
            summary = "Creates a booking",
            description = "validate the availability of a property and create a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "404", description = "resource not found request")
    })
    @PostMapping
    public ResponseEntity<BlockDTO> createBlock(@Valid @RequestBody Block block) {
        BlockDTO createdBlock = blockService.createBlock(block);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlock);
    }

    @Operation(
            summary = "Updates a block",
            description = "validate the availability of a property and update an existing block")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "404", description = "resource not found request")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BlockDTO> updateBlock(@PathVariable UUID id,@Valid @RequestBody Block block) {
        BlockDTO updatedBlock = blockService.updateBlock(id, block);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBlock);
    }

    @Operation(
            summary = "Deletes a block",
            description = "deletes an existing booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "404", description = "resource not found request")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlock(@PathVariable UUID id) {
        blockService.deleteBlock(id);
        return ResponseEntity.noContent().build();
    }
}


