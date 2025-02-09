package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.*;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.dto.response.CampSiteResponseDTO;
import com.group2.glamping.model.dto.response.ImageResponse;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.model.enums.CampSiteStatus;
import com.group2.glamping.model.mapper.CampSiteMapper;
import com.group2.glamping.repository.*;
import com.group2.glamping.service.interfaces.CampSiteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CampSiteServiceImpl implements CampSiteService {

    private final CampSiteRepository campSiteRepository;
    private final UserRepository userService;
    private final SelectionRepository selectionRepository;
    private final UtilityRepository utilityRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final CampTypeRepository campTypeRepository;


    @Override
    public List<CampSiteResponse> getAvailableCampSites() {
        return campSiteRepository.findAllByStatus(CampSiteStatus.Available).stream()
                .map(CampSiteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CampSiteResponse> getPendingCampSites() {
        return campSiteRepository.findAllByStatus(CampSiteStatus.Pending).stream()
                .map(CampSiteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CampSiteResponse> saveCampSite(CampSiteRequest campSiteUpdateRequest) {
        if (campSiteUpdateRequest == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        CampSite campSite = new CampSite();
        return getCampSiteResponse(campSite, campSiteUpdateRequest.getName(), campSiteUpdateRequest.getAddress(), campSiteUpdateRequest.getLatitude(), campSiteUpdateRequest.getLongitude(), campSiteUpdateRequest.getCampSiteSelections(), campSiteUpdateRequest.getCampSiteUtilities(), campSiteUpdateRequest.getCampSitePlaceTypes(), campSiteUpdateRequest.getCampTypeList(), campSiteUpdateRequest);
    }

    //

    private Optional<CampSiteResponse> getCampSiteResponse(CampSite campSite, String name, String address, double latitude, double longitude, List<SelectionRequest> campSiteSelections, List<UtilityRequest> campSiteUtilities, List<PlaceTypeRequest> campSitePlaceTypes, List<CampTypeUpdateRequest> campTypeList, CampSiteRequest campSiteUpdateRequest) {

        if (userService.findById(campSiteUpdateRequest.getHostId()).isPresent()) {
            campSite.setUser(userService.findById(campSiteUpdateRequest.getHostId()).get());
        } else {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        campSite.setName(name);
        campSite.setAddress(address);
        campSite.setLatitude(latitude);
        campSite.setLongitude(longitude);
        campSite.setStatus(CampSiteStatus.Pending);
        campSite.setCreatedTime(LocalDateTime.now());

        List<Selection> selections = campSiteSelections.stream()
                .map(request -> Selection.builder()
                        .name(request.name())
                        .description(request.description())
                        .price(request.price())
                        .imageUrl(request.image().getOriginalFilename())
                        .campSite(campSite)  // Gán CampSite vào Selection
                        .build()
                )
                .collect(Collectors.toList());

        campSite.setSelections(selections);


        List<Utility> utilities = campSiteUtilities.stream()
                .map(request -> utilityRepository.findById(request.id())
                                .map(existingUtility -> {
                                    existingUtility.setImageUrl(request.name());
//                            existingUtility.setStatus(request.isStatus());
                                    return utilityRepository.save(existingUtility);
                                })
                                .orElseGet(() -> {
                                    Utility newUtility = new Utility();
                                    newUtility.setName(request.name());
                                    newUtility.setImageUrl(request.imagePath().getOriginalFilename());
//                            newUtility.setStatus(request.isStatus());
                                    //newUtility.setCampSite(campSite);
                                    return utilityRepository.save(newUtility);
                                })
                )
                .collect(Collectors.toList());
        campSite.setUtilities(utilities);

        List<PlaceType> placeTypes = campSitePlaceTypes.stream()
                .map(request -> placeTypeRepository.findById(request.getId())
                        .map(existingPlaceType -> {
                            existingPlaceType.setImage(request.getImagePath());
                            existingPlaceType.setStatus(request.isStatus());
                            return placeTypeRepository.save(existingPlaceType);
                        })
                        .orElseGet(() -> {
                            PlaceType newPlaceType = new PlaceType();
                            newPlaceType.setName(request.getName());
                            newPlaceType.setImage(request.getImagePath());
                            newPlaceType.setStatus(request.isStatus());
                            //newPlaceType.setCampSite(campSite);
                            return placeTypeRepository.save(newPlaceType);
                        })
                )
                .collect(Collectors.toList());
        campSite.setPlaceTypes(placeTypes);

        List<CampType> campTypes = campTypeList.stream()
                .map(request -> campTypeRepository.findByTypeAndCampSiteId(request.getType(), campSite.getId())
                        .map(existingCampType -> {
                            existingCampType.setCapacity(request.getCapacity());
                            existingCampType.setPrice(request.getPrice());
                            existingCampType.setWeekendRate(request.getWeekendRate());
                            existingCampType.setHolidayRate(request.getHolidayRate());
                            existingCampType.setUpdatedTime(LocalDateTime.now());
                            existingCampType.setQuantity(request.getQuantity());
                            existingCampType.setStatus(request.isStatus());
                            return campTypeRepository.save(existingCampType);
                        })
                        .orElseGet(() -> {
                            CampType newCampType = CampType.builder()
                                    .type(request.getType())
                                    .capacity(request.getCapacity())
                                    .price(request.getPrice())
                                    .weekendRate(request.getWeekendRate())
                                    .holidayRate(request.getHolidayRate())
                                    .updatedTime(LocalDateTime.now())
                                    .quantity(request.getQuantity())
                                    .status(request.isStatus())
                                    .campSite(campSite)
                                    .build();
                            return campTypeRepository.save(newCampType);
                        })
                )
                .collect(Collectors.toList());
        campSite.setCampTypes(campTypes);

        return Optional.of(CampSiteMapper.toDto(campSiteRepository.save(campSite)));
    }


//    @Override
//    public Optional<CampSite> findCampSiteById(int id) {
//        return campSiteRepository.findById(id);
//    }


    @Override
    public Optional<CampSiteResponse> getCampSiteBasicDetail(int id) {
        return campSiteRepository.findById(id)
                .map(CampSiteMapper::toDto);
    }


    @Override
    public Optional<CampSiteResponse> updateCampSite(int id, CampSiteRequest campSiteUpdateRequest) {
        CampSite campSite = campSiteRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));

        return getCampSiteResponse(campSite, campSiteUpdateRequest.getName(), campSiteUpdateRequest.getAddress(), campSiteUpdateRequest.getLatitude(), campSiteUpdateRequest.getLongitude(), campSiteUpdateRequest.getCampSiteSelections(), campSiteUpdateRequest.getCampSiteUtilities(), campSiteUpdateRequest.getCampSitePlaceTypes(), campSiteUpdateRequest.getCampTypeList(), campSiteUpdateRequest);
    }


    @Override
    public Optional<CampSite> deleteCampSite(int id) {
        Optional<CampSite> existingCampSite = campSiteRepository.findById(id);

        if (existingCampSite.isPresent()) {
            CampSite campSite = existingCampSite.get();
            campSite.setStatus(CampSiteStatus.Not_Available);
            campSiteRepository.save(campSite);
            return Optional.of(campSite);
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

}

