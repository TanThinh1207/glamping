package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.entity.Camp;

import java.util.Optional;

public interface CampService {

    Optional<Camp> getCampById(int campId);
}
