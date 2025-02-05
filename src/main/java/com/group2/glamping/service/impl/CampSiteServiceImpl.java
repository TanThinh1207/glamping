package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.CampSiteCreateRequest;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.model.enums.CampSiteStatus;
import com.group2.glamping.model.mapper.CampSiteMapper;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.UserRepository;
import com.group2.glamping.service.interfaces.CampSiteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CampSiteServiceImpl implements CampSiteService {

    private final CampSiteRepository campSiteRepository;
    private final UserRepository userService;


    @Override
    public List<CampSiteResponse> getCampSites() {
        return campSiteRepository.findAll().stream()
                .map(CampSiteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CampSite> saveCampSite(CampSiteCreateRequest request) {
        if (userService.findById(request.getUserId()).isPresent()) {
            CampSite campSite = CampSite.builder()
                    .name(request.getName())
                    .address(request.getAddress())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .createdTime(LocalDateTime.now())
                    .user(userService.findById(request.getUserId()).get())
                    .imageList(request.getImageList())
                    .status(CampSiteStatus.Pending)
                    .campTypes(request.getCampTypeList())
                    .build();

            campSiteRepository.save(campSite);
            if (campSite.getCampTypes() != null) {
                for (CampType campType : campSite.getCampTypes()) {
                    campType.setCampSite(campSite);
                    campType.setUpdatedTime(LocalDateTime.now());
                    campType.setStatus(true);
                }
            }
            campSiteRepository.save(campSite);
            return Optional.of(campSite);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CampSite> findCampSiteById(int id) {
        return campSiteRepository.findById(id);
    }


    @Override
    public Optional<CampSiteResponse> getCampSiteBasicDetail(int id) {
        return campSiteRepository.findById(id)
                .map(CampSiteMapper::toDto);
    }


    @Override
    public void updateCampSite(CampSite campSite) {
        campSiteRepository.save(campSite);
    }

    @Override
    public void deleteCampSite(CampSite campSite) {
        campSite.setStatus(CampSiteStatus.Not_Available);
        campSiteRepository.save(campSite);
    }
}
