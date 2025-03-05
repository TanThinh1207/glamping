package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.requests.CampSiteUpdateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.model.enums.CampSiteStatus;
import com.group2.glamping.model.mapper.CampSiteMapper;
import com.group2.glamping.repository.*;
import com.group2.glamping.service.interfaces.CampSiteService;
import com.group2.glamping.utils.ResponseFilterUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CampSiteServiceImpl implements CampSiteService {

    private final CampSiteRepository campSiteRepository;
    private final UserRepository userService;
    private final UtilityRepository utilityRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final CampTypeRepository campTypeRepository;
    //    private final S3Service s3Service;
    private final CampSiteMapper campSiteMapper;
    private final FacilityRepository facilityRepository;

    @Override
    public Optional<CampSiteResponse> saveCampSite(CampSiteRequest campSiteUpdateRequest) {
        if (campSiteUpdateRequest == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        CampSite campSite = new CampSite();
        return getCampSiteResponse(campSite,
                campSiteUpdateRequest.hostId(),
                campSiteUpdateRequest.name(),
                campSiteUpdateRequest.address(),
                campSiteUpdateRequest.city(),
                campSiteUpdateRequest.latitude(),
                campSiteUpdateRequest.longitude(),
                campSiteUpdateRequest.description(),
                campSiteUpdateRequest.campSiteSelections(),
                campSiteUpdateRequest.placeTypeIds(),
                campSiteUpdateRequest.utilityIds(),
                campSiteUpdateRequest.campTypeList());
    }


    //

    private Optional<CampSiteResponse> getCampSiteResponse(CampSite campSite,
                                                           int hostId,
                                                           String name,
                                                           String address,
                                                           String city,
                                                           double latitude,
                                                           double longitude,
                                                           String description,
                                                           List<SelectionRequest> campSiteSelections,
                                                           List<Integer> campSiteUtilities,
                                                           List<Integer> campSitePlaceTypes,
                                                           List<CampTypeUpdateRequest> campTypeList) {

        // Check Host
        campSite.setUser(userService.findById(hostId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));

        // Set basic in4
        campSite.setName(name);
        campSite.setAddress(address);
        campSite.setLatitude(latitude);
        campSite.setLongitude(longitude);
        campSite.setStatus(CampSiteStatus.Pending);
        campSite.setCreatedTime(LocalDateTime.now());
        campSite.setCity(city);
        campSite.setDescription(description);

        campSiteRepository.save(campSite);

        // Selection
        List<Selection> selections = campSiteSelections.stream()
                .map(request -> Selection.builder()
                        .name(request.name())
                        .description(request.description())
                        .price(request.price())
                        .campSite(campSite)
                        .build())
                .collect(Collectors.toList());

        campSite.setSelections(selections);

        List<CampType> campTypes = campTypeList.stream()
                .map(request -> campTypeRepository.findByTypeAndCampSiteId(request.type(), campSite.getId())
                        .map(existingCampType -> {
                            existingCampType.setCapacity(request.capacity());
                            existingCampType.setPrice(request.price());
                            existingCampType.setWeekendRate(request.weekendRate());
                            existingCampType.setUpdatedTime(LocalDateTime.now());
                            existingCampType.setQuantity(request.quantity());
                            existingCampType.setStatus(request.status());
                            existingCampType.setFacilities(facilityRepository.findAllById(request.facilities()));
                            return campTypeRepository.save(existingCampType);
                        })
                        .orElseGet(() -> {
                            CampType newCampType = CampType.builder()
                                    .type(request.type())
                                    .capacity(request.capacity())
                                    .price(request.price())
                                    .weekendRate(request.weekendRate())
                                    .updatedTime(LocalDateTime.now())
                                    .quantity(request.quantity())
                                    .status(request.status())
                                    .campSite(campSite)
                                    .facilities(facilityRepository.findAllById(request.facilities()))
                                    .build();
                            return campTypeRepository.save(newCampType);
                        }))
                .collect(Collectors.toList());

        campSite.setCampTypes(campTypes);

        //  Utilities
        List<Utility> utilities = campSiteUtilities.stream()
                .map(request -> utilityRepository.findById(request)
                        .orElseThrow(() -> new AppException(ErrorCode.UTILITY_NOT_FOUND)))
                .collect(Collectors.toList());
        campSite.setUtilities(utilities);

        // PlaceTypes
        List<PlaceType> placeTypes = campSitePlaceTypes.stream()
                .map(request -> placeTypeRepository.findById(request)
                        .orElseThrow(() -> new AppException(ErrorCode.PLACE_TYPE_NOT_FOUND)))
                .collect(Collectors.toList());
        campSite.setPlaceTypes(placeTypes);


        return Optional.of(campSiteMapper.toDto(campSiteRepository.save(campSite)));
    }

    @Override
    public void updateCampSite(int id,
                               CampSiteUpdateRequest campSiteUpdateRequest,
                               List<MultipartFile> files) {
        CampSite campSite = campSiteRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));

        updateProperties(campSite, campSiteUpdateRequest);

        campSiteMapper.toDto(campSiteRepository.save(campSite));
    }

    private void updateProperties(CampSite campSite, CampSiteUpdateRequest campSiteUpdateRequest) {
        if (campSiteUpdateRequest.name() != null) {
            campSite.setName(campSiteUpdateRequest.name());
        }
        if (campSiteUpdateRequest.status() != null) {
            campSite.setStatus(campSiteUpdateRequest.status());
        }
        if (campSiteUpdateRequest.address() != null) {
            campSite.setAddress(campSiteUpdateRequest.address());
        }
    }


    @Override
    public void deleteCampSite(int id) {
        Optional<CampSite> existingCampSite = campSiteRepository.findById(id);

        if (existingCampSite.isPresent()) {
            CampSite campSite = existingCampSite.get();
            campSite.setStatus(CampSiteStatus.Not_Available);
            campSiteRepository.save(campSite);
        } else {
            throw new AppException(ErrorCode.CAMP_SITE_NOT_FOUND);
        }
    }

    @Override
    public PagingResponse<?> getCampSites(Map<String, String> params, int page, int size, String sortBy, String direction) {
        Specification<CampSite> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                switch (key) {
                    case "id":
                        predicates.add(criteriaBuilder.equal(root.get("id"), value));
                        break;
                    case "name":
                        predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                        break;
                    case "status":
                        predicates.add(criteriaBuilder.equal(root.get("status"), value));
                        break;
                    case "city":
                        predicates.add(criteriaBuilder.like(root.get("city"), "%" + value + "%"));
                        break;
                    case "address":
                        predicates.add(criteriaBuilder.like(root.get("address"), "%" + value + "%"));
                        break;
                    case "createdTime":
                        predicates.add(criteriaBuilder.equal(root.get("createdTime"), value));
                        break;
                    case "depositRate":
                        predicates.add(criteriaBuilder.equal(root.get("depositRate"), value));
                        break;
                    case "latitude":
                        predicates.add(criteriaBuilder.equal(root.get("latitude"), value));
                        break;
                    case "longitude":
                        predicates.add(criteriaBuilder.equal(root.get("longitude"), value));
                        break;
                    case "placeTypeName":
                        List<String> placeTypeList = Arrays.asList(value.split(","));
                        Join<CampSite, PlaceType> placeTypeJoin = root.join("placeTypes");
                        predicates.add(placeTypeJoin.get("name").in(placeTypeList));
                        break;
                    case "utilityName":
                        List<String> utilityNameList = Arrays.asList(value.split(","));
                        Join<CampSite, Utility> utilityJoin = root.join("utilities");
                        predicates.add(utilityJoin.get("name").in(utilityNameList));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CampSite> campSitePage = campSiteRepository.findAll(spec, pageable);
        List<CampSiteResponse> campSiteResponses = campSitePage.getContent().stream()
                .map(campSiteMapper::toDto)
                .toList();

        return new PagingResponse<>(
                campSiteResponses,
                campSitePage.getTotalElements(),
                campSitePage.getTotalPages(),
                campSitePage.getNumber(),
                campSitePage.getNumberOfElements()
        );
    }


    @Override
    public Object getFilteredCampSites(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) {
        PagingResponse<?> campSites = getCampSites(params, page, size, sortBy, direction);
        return ResponseFilterUtil.getFilteredResponse(fields, campSites, "Retrieve filtered list successfully");
    }


}


