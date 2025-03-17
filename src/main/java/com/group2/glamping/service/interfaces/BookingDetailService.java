package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.response.BookingDetailResponse;
import com.group2.glamping.model.dto.response.CampTypeItemResponse;

import java.util.List;

public interface BookingDetailService {

    BookingDetailResponse assignCamp(Integer bookingDetailId, Integer campId);

    List<CampTypeItemResponse> groupBookingDetailsByCampType(List<BookingDetailResponse> bookingDetails);
}
