package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.response.BookingDetailResponse;
import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.model.entity.Camp;
import com.group2.glamping.repository.BookingDetailRepository;
import com.group2.glamping.repository.CampRepository;
import com.group2.glamping.service.interfaces.BookingDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingDetailServiceImpl implements BookingDetailService
{
    private final BookingDetailRepository bookingDetailRepository;
    private final CampRepository campRepository;

    @Override
    public BookingDetailResponse checkInBookingDetail(Integer bookingDetailId) {
        BookingDetail bookingDetail = bookingDetailRepository.findById(bookingDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_DETAIL_NOT_FOUND));

        bookingDetail.setCheckInTime(LocalDateTime.now());
        bookingDetailRepository.save(bookingDetail); // Ensure changes are saved

        return BookingDetailResponse.fromEntity(bookingDetail);
    }

    @Override
    public BookingDetailResponse assignCamp(Integer bookingDetailId, Integer campId) {
        BookingDetail bookingDetail = bookingDetailRepository.findById(bookingDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_DETAIL_NOT_FOUND));

        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new AppException(ErrorCode.CAMP_NOT_FOUND));

        bookingDetail.setCamp(camp);
        bookingDetailRepository.save(bookingDetail); // Ensure changes are saved

        return BookingDetailResponse.fromEntity(bookingDetail);
    }
}
