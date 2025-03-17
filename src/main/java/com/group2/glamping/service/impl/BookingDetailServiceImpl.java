package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.response.BookingDetailResponse;
import com.group2.glamping.model.dto.response.CampTypeItemResponse;
import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.model.entity.Camp;
import com.group2.glamping.repository.BookingDetailRepository;
import com.group2.glamping.repository.CampRepository;
import com.group2.glamping.service.interfaces.BookingDetailService;
import com.group2.glamping.service.interfaces.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingDetailServiceImpl implements BookingDetailService {
    private final BookingDetailRepository bookingDetailRepository;
    private final CampRepository campRepository;
    private final S3Service s3Service;

    @Override
    public BookingDetailResponse assignCamp(Integer bookingDetailId, Integer campId) {
        BookingDetail bookingDetail = bookingDetailRepository.findById(bookingDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_DETAIL_NOT_FOUND));

        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new AppException(ErrorCode.CAMP_NOT_FOUND));

        bookingDetail.setCamp(camp);
        bookingDetailRepository.save(bookingDetail);

        return BookingDetailResponse.fromEntity(bookingDetail, s3Service);
    }

    @Override
    public List<CampTypeItemResponse> groupBookingDetailsByCampType(List<BookingDetailResponse> bookingDetails) {
        Map<Integer, CampTypeItemResponse> campTypeMap = new HashMap<>();

        for (BookingDetailResponse detail : bookingDetails) {
            int campTypeId = detail.getCampTypeResponse().getId();

            if (campTypeMap.containsKey(campTypeId)) {
                CampTypeItemResponse existing = campTypeMap.get(campTypeId);
                existing.setQuantity(existing.getQuantity() + 1);
                existing.setTotal(existing.getTotal() + detail.getAmount());
            } else {
                CampTypeItemResponse newItem = new CampTypeItemResponse();
                newItem.setBookingDetail(detail);
                newItem.setQuantity(1);
                newItem.setTotal(detail.getAmount());
                campTypeMap.put(campTypeId, newItem);
            }
        }

        return new ArrayList<>(campTypeMap.values());
    }

}
