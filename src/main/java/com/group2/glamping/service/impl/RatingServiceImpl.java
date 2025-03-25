package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.response.FullRatingResponse;
import com.group2.glamping.model.dto.response.PagingResponseSingle;
import com.group2.glamping.model.dto.response.RatingResponse;
import com.group2.glamping.repository.BookingRepository;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.service.interfaces.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {

    private final BookingRepository bookingRepository;
    private final CampSiteRepository campSiteRepository;

    @Override
    public PagingResponseSingle<FullRatingResponse> getFullRating(
            Integer campSiteId,
            int page,
            int size,
            String sortField,
            String sortDirection) {

        if (!campSiteRepository.existsById(campSiteId)) {
            throw new AppException(ErrorCode.CAMP_SITE_NOT_FOUND);
        }

        Sort.Direction direction = Sort.Direction.DESC;
        if ("asc".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.ASC;
        }

        String validSortField = switch (sortField) {
            case "userId" -> "user.id";
            case "userName" -> "user.firstname";
            case "uploadTime", "checkOutTime" -> "checkOutTime";
            case "rating" -> "rating";
            default -> "checkOutTime";
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, validSortField));
        Page<RatingResponse> ratingPage = bookingRepository.findAllRatingsByCampSiteId(campSiteId, pageable);
        double averageRating = bookingRepository.findAverageRatingByCampSiteId(campSiteId);
        double roundedAverageRating = BigDecimal.valueOf(averageRating)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        FullRatingResponse fullRatingResponse = FullRatingResponse.builder()
                .averageRating(roundedAverageRating)
                .ratingResponseList(ratingPage.getContent())
                .build();
        return new PagingResponseSingle<>(
                ratingPage.getTotalElements(),
                ratingPage.getTotalPages(),
                ratingPage.getNumber(),
                ratingPage.getSize(),
                fullRatingResponse
        );
    }
}



