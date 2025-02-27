package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.response.BookingDetailResponse;

public interface BookingDetailService {

    BookingDetailResponse checkInBookingDetail(Integer bookingDetailId);

    BookingDetailResponse assignCamp(Integer bookingDetailId, Integer campId);
}
