package com.group2.glamping.service.impl;

import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.repository.BookingRepository;
import com.group2.glamping.repository.CampTypeRepository;
import com.group2.glamping.service.interfaces.ICampTypeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Data
@Service
public class CampTypeServiceImpl implements ICampTypeService {
    @Autowired
    private final CampTypeRepository campTypeRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Override
    public Integer findAvailableSlots(Integer campTypeId, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
        CampType campType = campTypeRepository.findById(campTypeId)
                .orElseThrow(() -> new RuntimeException("Camp type not found"));
        Integer bookedQuantity = bookingRepository.findAvailableSlots(campTypeId, checkInDate, checkOutDate);

        if (bookedQuantity == null) {
            bookedQuantity = 0;
        }

        return bookedQuantity;
    }
}

