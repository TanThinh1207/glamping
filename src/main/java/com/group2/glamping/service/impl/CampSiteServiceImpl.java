package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.*;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.model.enums.CampSiteStatus;
import com.group2.glamping.model.mapper.CampSiteMapper;
import com.group2.glamping.repository.*;
import com.group2.glamping.service.interfaces.CampSiteService;
import com.group2.glamping.service.interfaces.S3Service;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final S3Service s3Service;
    private final CampSiteMapper campSiteMapper;


    @Override
    public List<CampSiteResponse> getCampSiteByStatus(CampSiteStatus status) {
        if (campSiteRepository.findAllByStatus(status).isEmpty()) {
            return Collections.emptyList();
        }
        return campSiteRepository.findAllByStatus(status).stream()
                .map(campSiteMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<CampSiteResponse> saveCampSite(CampSiteRequest campSiteUpdateRequest,
                                                   List<MultipartFile> files,
                                                   MultipartFile selectionFile,
                                                   MultipartFile campTypeFile) {
        if (campSiteUpdateRequest == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        CampSite campSite = new CampSite();
        return getCampSiteResponse(campSite,
                campSiteUpdateRequest.hostId(),
                campSiteUpdateRequest.name(),
                campSiteUpdateRequest.address(),
                campSiteUpdateRequest.city(),
                files,
                campSiteUpdateRequest.latitude(),
                campSiteUpdateRequest.longitude(),
                campSiteUpdateRequest.campSiteSelections(),
                campSiteUpdateRequest.campSiteUtilities(),
                campSiteUpdateRequest.campSitePlaceTypes(),
                campSiteUpdateRequest.campTypeList(),
                selectionFile,
                campTypeFile);
    }


    //

    private Optional<CampSiteResponse> getCampSiteResponse(CampSite campSite,
                                                           int hostId,
                                                           String name,
                                                           String address,
                                                           String city,
                                                           List<MultipartFile> files,
                                                           double latitude,
                                                           double longitude,
                                                           List<SelectionRequest> campSiteSelections,
                                                           List<Integer> campSiteUtilities,
                                                           List<Integer> campSitePlaceTypes,
                                                           List<CampTypeUpdateRequest> campTypeList,
                                                           MultipartFile selectionFile,
                                                           MultipartFile campTypeFile) {

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

        List<Image> imageEntities;

        // CampSite Image
        if (files != null && !files.isEmpty()) {
            imageEntities = files.stream()
                    .map(file -> {
                        String imageUrl = s3Service.uploadFile(file, "CampSite/" + campSite.getName(), "CAMP_SITE");
                        return new Image(0, imageUrl, campSite);
                    })
                    .collect(Collectors.toList());
            campSite.setImageList(imageEntities);

        } else {
            System.out.println("File is empty");
        }

        // Selection
        String selectionImageUrl = selectionFile != null
                ? s3Service.uploadFile(selectionFile, "CampSite/" + campSite.getName(), "SELECTION")
                : null;

        List<Selection> selections = campSiteSelections.stream()
                .map(request -> Selection.builder()
                        .name(request.name())
                        .description(request.description())
                        .price(request.price())
                        .campSite(campSite)
                        .imageUrl(selectionImageUrl)
                        .build())
                .collect(Collectors.toList());

        campSite.setSelections(selections);

        // CampType
        String campTypeImageUrl = campTypeFile != null
                ? s3Service.uploadFile(campTypeFile, "CampSite/" + campSite.getName(), "CAMP_TYPE")
                : null;

        List<CampType> campTypes = campTypeList.stream()
                .map(request -> campTypeRepository.findByTypeAndCampSiteId(request.type(), campSite.getId())
                        .map(existingCampType -> {
                            existingCampType.setCapacity(request.capacity());
                            existingCampType.setPrice(request.price());
                            existingCampType.setWeekendRate(request.weekendRate());
                            existingCampType.setUpdatedTime(LocalDateTime.now());
                            existingCampType.setQuantity(request.quantity());
                            existingCampType.setStatus(request.status());

                            if (campTypeImageUrl != null) {
                                existingCampType.setImage(campTypeImageUrl);
                            }

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
                                    .image(campTypeImageUrl)
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
    public Optional<CampSiteResponse> getCampSiteBasicDetail(int id) {
        return campSiteRepository.findById(id)
                .map(campSiteMapper::toDto);
    }


//    @Override
//    public Optional<CampSiteResponse> updateCampSite(int id, CampSiteRequest campSiteUpdateRequest) {
//        CampSite campSite = campSiteRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));
//
//        return getCampSiteResponse(campSite,
//                campSiteUpdateRequest.hostId(),
//                campSiteUpdateRequest.name(),
//                campSiteUpdateRequest.address(),
//                campSiteUpdateRequest.city(),
//                files,
//                campSiteUpdateRequest.latitude(),
//                campSiteUpdateRequest.longitude(),
//                campSiteUpdateRequest.campSiteSelections(),
//                campSiteUpdateRequest.campSiteUtilities(),
//                campSiteUpdateRequest.campSitePlaceTypes(),
//                campSiteUpdateRequest.campTypeList(),
//                selectionFile,
//                campTypeFile);
//    }


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
    public BaseResponse searchCampSiteByNameOrCity(String str) {
        BaseResponse response = new BaseResponse();

        // CHECK INPUT
        if (str == null || str.trim().isEmpty()) {
            response.setStatusCode(400);
            response.setMessage("Search keyword cannot be empty");
            response.setData(Collections.emptyList());
            return response;
        }

        List<CampSite> campSites = campSiteRepository
                .findByNameContainingIgnoreCaseOrCityContainingIgnoreCase(str, str);

        if (campSites.isEmpty()) {
            response.setStatusCode(404);
            response.setMessage("No campsites found for the given search keyword");
            response.setData(Collections.emptyList());
            return response;
        }

        // Chuyển đổi từ entity sang DTO
        List<CampSiteResponseDTO> campSiteResponseList = campSites.stream().map(campSite -> {
            CampSiteResponseDTO campSiteResponse = new CampSiteResponseDTO();
            campSiteResponse.setId(campSite.getId());
            campSiteResponse.setName(campSite.getName());
            campSiteResponse.setAddress(campSite.getAddress());

            // Chuyển danh sách Image sang danh sách ImageResponse
            List<ImageResponse> imageResponses = campSite.getImageList().stream()
                    .map(image -> new ImageResponse(image.getId(), image.getPath()))
                    .collect(Collectors.toList());

            campSiteResponse.setImageList(imageResponses);
            campSiteResponse.setCreatedTime(campSite.getCreatedTime());
            campSiteResponse.setStatus(campSite.getStatus());
            return campSiteResponse;
        }).collect(Collectors.toList());

        // Trả về response
        response.setStatusCode(200);
        response.setMessage("Campsites found");
        response.setData(campSiteResponseList);

        return response;
    }

    @Override
    public Optional<CampSiteResponse> enableCampSite(int id) {
        Optional<CampSite> existingCampSite = campSiteRepository.findById(id);
        if (existingCampSite.isPresent() && existingCampSite.get().getStatus() == CampSiteStatus.Not_Available) {
            CampSite campSite = existingCampSite.get();
            campSite.setStatus(CampSiteStatus.Available);
            return Optional.of(campSiteMapper.toDto(campSiteRepository.save(campSite)));
        }
        return Optional.empty();
    }

    @Override
    public PagingResponse<?> getCampSites(Map<String, String> params, int page, int size) {
        Specification<CampSite> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                switch (key) {
                    case "name":
                        predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                        break;
                    case "status":
                        predicates.add(criteriaBuilder.equal(root.get("status"), CampSiteStatus.Available));
                        break;
                    case "city":
                        predicates.add(criteriaBuilder.like(root.get("address"), "%" + value + "%"));
                        break;
                    case "placeTypeName":
                        List<String> placeTypeList = Arrays.asList(value.split(","));
                        Join<CampSite, PlaceType> placeTypeJoin = root.join("placeTypes");
                        predicates.add(placeTypeJoin.get("name").in(placeTypeList));
                        break;
                }
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page, size);
        Page<CampSite> campSitePage = campSiteRepository.findAll(spec, pageable);
        List<CampSiteResponse> campSiteResponses = campSitePage.getContent().stream()
                .map(campSiteMapper::toDto)
                .toList();
//        if (params.containsKey("display")) {
//            Set<String> allowedFields = Set.of(params.get("display").split(","));
//            List<ObjectNode> filteredResponses = ResponseFilterUtil.filterFields(campSiteResponses, allowedFields);
//
//            return new PagingResponse<>(
//                    filteredResponses,
//                    campSitePage.getTotalElements(),
//                    campSitePage.getTotalPages(),
//                    campSitePage.getNumber(),
//                    campSitePage.getNumberOfElements()
//            );
//        }


        return new PagingResponse<>(
                campSiteResponses,
                campSitePage.getTotalElements(),
                campSitePage.getTotalPages(),
                campSitePage.getNumber(),
                campSitePage.getNumberOfElements()
        );

    }
}


