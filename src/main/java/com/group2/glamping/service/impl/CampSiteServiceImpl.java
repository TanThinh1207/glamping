package com.group2.glamping.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.requests.CampSiteUpdateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.model.enums.CampSiteStatus;
import com.group2.glamping.model.enums.CampStatus;
import com.group2.glamping.model.mapper.CampSiteMapper;
import com.group2.glamping.repository.*;
import com.group2.glamping.service.interfaces.CampSiteService;
import com.group2.glamping.utils.JsonUtil;
import com.group2.glamping.utils.RedisUtil;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.group2.glamping.utils.JsonUtil.deserializePagingResponse;
import static com.group2.glamping.utils.JsonUtil.serializePagingResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class CampSiteServiceImpl implements CampSiteService {

    private final CampSiteRepository campSiteRepository;
    private final UserRepository userService;
    private final UtilityRepository utilityRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final CampTypeRepository campTypeRepository;
    private final CampSiteMapper campSiteMapper;
    private final FacilityRepository facilityRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedisUtil redisUtil;
    private final CampRepository campRepository;

    @Override
    public Optional<CampSiteResponse> saveCampSite(CampSiteRequest campSiteUpdateRequest) {
        if (campSiteUpdateRequest == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        redisUtil.deleteCache("filteredCampSites:*");
        redisUtil.deleteCache("campSites:*");
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
                campSiteUpdateRequest.utilityIds(),
                campSiteUpdateRequest.placeTypeIds(),
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
        campSite.setDepositRate(0.3);
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
                            generateCamp(existingCampType);
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
                            generateCamp(newCampType);
                            return campTypeRepository.save(newCampType);
                        }))
                .collect(Collectors.toList());

        campSite.setCampTypes(campTypes);

        //  Utilities
        System.out.println(campSiteUtilities);
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

    private void generateCamp(CampType campType) {
        List<Camp> camps = new ArrayList<>();
        for (int i = 0; i <= campType.getQuantity(); i++) {
            camps.add(Camp.builder()
                    .campType(campType)
                    .createdTime(LocalDateTime.now())
                    .status(CampStatus.Not_Assigned)
                    .updatedTime(LocalDateTime.now())
                    .name(campType.getType() + " " + i)
                    .build());
        }
        campRepository.saveAll(camps);
    }

    @Override
    public Object updateCampSite(int id, CampSiteUpdateRequest campSiteUpdateRequest) {
        redisUtil.deleteCache("filteredCampSites:*");
        redisUtil.deleteCache("campSites:*");

        CampSite campSite = campSiteRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));

        if (campSiteUpdateRequest.getName() != null) {
            campSite.setName(campSiteUpdateRequest.getName());
        }
        if (campSiteUpdateRequest.getDescription() != null) {
            campSite.setDescription(campSiteUpdateRequest.getDescription());
        }
        if (campSiteUpdateRequest.getDepositRate() != null) {
            campSite.setDepositRate(campSiteUpdateRequest.getDepositRate());
        }
        if (campSiteUpdateRequest.getStatus() != null) {
            campSite.setStatus(campSiteUpdateRequest.getStatus());
        }
        if (campSiteUpdateRequest.getLatitude() != null) {
            campSite.setLatitude(campSiteUpdateRequest.getLatitude());
        }
        if (campSiteUpdateRequest.getLongitude() != null) {
            campSite.setLongitude(campSiteUpdateRequest.getLongitude());
        }
        if (campSiteUpdateRequest.getCity() != null) {
            campSite.setCity(campSiteUpdateRequest.getCity());
        }
        if (campSiteUpdateRequest.getAddress() != null) {
            campSite.setAddress(campSiteUpdateRequest.getAddress());
        }
        if (campSiteUpdateRequest.getMessage() != null) {
            campSite.setMessage(campSiteUpdateRequest.getMessage());
        }
        CampSite updatedCampSite = campSiteRepository.save(campSite);

        return campSiteMapper.toDto(updatedCampSite);
    }


    @Override
    public void deleteCampSite(int id) {
        redisUtil.deleteCache("filteredCampSites:*");
        redisUtil.deleteCache("campSites:*");
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
    public PagingResponse<?> getCampSites(Map<String, String> params, int page, int size, String sortBy, String direction) throws JsonProcessingException {
        String sortedParams = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        String cacheKey = String.format("filteredCampSites:%s:%d:%d:%s:%s", sortedParams, page, size, sortBy, direction);
        String cachedData = redisTemplate.opsForValue().get(cacheKey);


        if (cachedData != null) {
            System.out.println("Cache hit! in deserializePagingResponse");
            return deserializePagingResponse(cachedData);
        }

        Specification<CampSite> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
//            root.fetch("campTypes", JoinType.LEFT);

            params.forEach((key, value) -> {
                switch (key) {
                    case "id" -> predicates.add(criteriaBuilder.equal(root.get("id"), value));
                    case "name" -> predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                    case "status" -> predicates.add(criteriaBuilder.equal(root.get("status"), value));
                    case "city" -> predicates.add(criteriaBuilder.like(root.get("city"), "%" + value + "%"));
                    case "address" -> predicates.add(criteriaBuilder.like(root.get("address"), "%" + value + "%"));
                    case "placeTypeName" -> {
                        List<String> placeTypeList = Arrays.asList(value.split(","));
                        Join<CampSite, PlaceType> placeTypeJoin = root.join("placeTypes");
                        predicates.add(placeTypeJoin.get("name").in(placeTypeList));
                    }
                    case "utilityName" -> {
                        List<String> utilityNameList = Arrays.asList(value.split(","));
                        Join<CampSite, Utility> utilityJoin = root.join("utilities");
                        predicates.add(utilityJoin.get("name").in(utilityNameList));
                    }
                    case "userId" -> {
                        Join<CampSite, User> userJoin = root.join("user");
                        predicates.add(criteriaBuilder.equal(userJoin.get("id"), value));
                    }
                }
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CampSite> campSitePage = campSiteRepository.findAll(spec, pageable);

        List<CampSiteResponse> campSiteResponses = campSitePage.getContent().stream()
                .map(campSiteMapper::toDto)
                .collect(Collectors.toList());

        PagingResponse<?> response = new PagingResponse<>(campSiteResponses, campSitePage.getTotalElements(), campSitePage.getTotalPages(), campSitePage.getNumber(), campSitePage.getNumberOfElements());

        redisTemplate.opsForValue().set(cacheKey, serializePagingResponse(response));

        return response;
    }


    @Override
    public Object getFilteredCampSites(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) throws JsonProcessingException {
        String cacheKey = String.format("filteredCampSites:%s:%d:%d:%s:%s:%s", params.toString(), page, size, fields, sortBy, direction);
        String cachedData = redisTemplate.opsForValue().get(cacheKey);
        System.out.println(cacheKey);
        if (cachedData != null) {
            System.out.println("Cache hit in: getFilteredCampSites ");
            JsonNode rootNode = JsonUtil.getObjectMapper().readTree(cachedData);
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null) {
                PagingResponse<CampSiteResponse> cachedResponse = JsonUtil.getObjectMapper().readValue(dataNode.toString(), new TypeReference<>() {
                });

                if (fields != null && !fields.isEmpty()) {
                    System.out.println("Cache hit with dynamic fields");
                    return ResponseFilterUtil.getFilteredResponse(fields, cachedResponse, "Return successfully");
                }

                return BaseResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Return successfully")
                        .data(cachedResponse)
                        .build();
            } else {
                throw new JsonProcessingException("Missing 'data' field in cached response") {
                };
            }
        }

        PagingResponse<?> campSites = getCampSites(params, page, size, sortBy, direction);
        Object filteredResponse = ResponseFilterUtil.getFilteredResponse(fields, campSites, "Return using dynamic filter successfully");

        String filteredResponseJson = JsonUtil.getObjectMapper().writeValueAsString(filteredResponse);
        redisTemplate.opsForValue().set(cacheKey, filteredResponseJson);

        return filteredResponse;
    }

    @Override
    public CampSiteResponse updatePlaceType(int campSiteId, List<Integer> placeTypeIds) {
        CampSite campSite = campSiteRepository.findById(campSiteId)
                .orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));

        if (placeTypeIds == null || placeTypeIds.isEmpty()) {
            throw new AppException(ErrorCode.PLACE_TYPE_LIST_CANNOT_BE_EMPTY);
        }

        List<PlaceType> placeTypes = placeTypeRepository.findAllById(placeTypeIds);

        if (placeTypes.isEmpty()) {
            throw new AppException(ErrorCode.PLACE_TYPE_NOT_FOUND);
        }

        Set<PlaceType> currentPlaceTypes = new HashSet<>(campSite.getPlaceTypes());
        Set<PlaceType> newPlaceTypes = new HashSet<>(placeTypes);

        if (!currentPlaceTypes.equals(newPlaceTypes)) {
            campSite.setPlaceTypes(placeTypes);
            campSiteRepository.save(campSite);
        }

        return campSiteMapper.toDto(campSite);
    }

    @Override
    public CampSiteResponse updateUtility(int campSiteId, List<Integer> utilityIds) {
        CampSite campSite = campSiteRepository.findById(campSiteId)
                .orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));

        if (utilityIds == null || utilityIds.isEmpty()) {
            throw new AppException(ErrorCode.UTILITY_LIST_CANNOT_BE_EMPTY);
        }

        List<Utility> utilities = utilityRepository.findAllById(utilityIds);

        if (utilities.isEmpty()) {
            throw new AppException(ErrorCode.UTILITY_NOT_FOUND);
        }

        Set<Utility> currentUtilities = new HashSet<>(campSite.getUtilities());
        Set<Utility> newUtilities = new HashSet<>(utilities);

        if (!currentUtilities.equals(newUtilities)) {
            campSite.setUtilities(utilities);
            campSiteRepository.save(campSite);
        }

        return campSiteMapper.toDto(campSite);
    }


}


