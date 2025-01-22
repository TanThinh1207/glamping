package com.group2.glamping.service.impl;

import com.group2.glamping.service.interfaces.CampTypeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Data
@Service
public class CampTypeServiceImpl implements CampTypeService {
}

