package com.group2.glamping.service.impl;

import com.group2.glamping.model.entity.Camp;
import com.group2.glamping.repository.CampRepository;
import com.group2.glamping.service.interfaces.CampService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CampServiceImpl implements CampService {

    private final CampRepository campRepository;

    @Override
    public Optional<Camp> getCampById(int campId) {
        return Optional.ofNullable(campRepository.findById(campId)
                .orElseThrow(() -> new EntityNotFoundException("Camp not found with ID: " + campId)));
    }
}


