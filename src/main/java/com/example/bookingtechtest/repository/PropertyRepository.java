package com.example.bookingtechtest.repository;

import com.example.bookingtechtest.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

}
