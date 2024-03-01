package com.example.bookingtechtest.service;

import com.example.bookingtechtest.dto.BlockDTO;
import com.example.bookingtechtest.entity.Block;
import com.example.bookingtechtest.exception.ResourceNotFoundException;
import com.example.bookingtechtest.repository.BlockRepository;
import com.example.bookingtechtest.validator.PropertyAvailabilityValidator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BlockService {
    private final BlockRepository blockRepository;
    private ModelMapper modelMapper;

    private PropertyAvailabilityValidator propertyAvailabilityValidator;

    public BlockService(BlockRepository blockRepository,  ModelMapper modelMapper, PropertyAvailabilityValidator propertyAvailabilityValidator) {
        this.blockRepository = blockRepository;
        this.modelMapper = modelMapper;
        this.propertyAvailabilityValidator = propertyAvailabilityValidator;
    }

    public BlockDTO createBlock(Block block) {
        // Logic to save the block in the database
        block.setCreated_at(LocalDateTime.now());
        propertyAvailabilityValidator.validateBlockPropertyAvailability(block.getStartDate(),block.getEndDate(), block.getProperty());

        blockRepository.save(block);
        return modelMapper.map(block, BlockDTO.class);
    }

    public BlockDTO updateBlock(UUID id, Block updatedBlock) {
        // Logic to check if the block with the given id exists
        Block existingBlock = getBlock(id);

        // Update the existing block details with the new details
        existingBlock.setStartDate(updatedBlock.getStartDate());
        existingBlock.setEndDate(updatedBlock.getEndDate());

        propertyAvailabilityValidator.validateBlockPropertyAvailability(updatedBlock.getStartDate(),updatedBlock.getEndDate(), updatedBlock.getProperty());

        existingBlock.setLast_updated_at(LocalDateTime.now());
        blockRepository.save(existingBlock);

        return modelMapper.map(updatedBlock, BlockDTO.class);
    }

    public void deleteBlock(UUID id) {
        // Logic to check if the block with the given id exists
        Block existingBlock = getBlock(id);

        // Delete the block from the database
        blockRepository.delete(existingBlock);
    }

    public Block getBlock(UUID id) {
        // Logic to retrieve the block with the given id from the database
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + id));
    }
}


