package com.group2.glamping.model.dto.response;

import com.group2.glamping.model.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    int id;
    UserResponse user;
    CampSiteResponse campSite;
    LocalDateTime created_at;
    BookingStatus status;
    double totalAmount;
    List<BookingDetailResponse> bookingDetailResponseList;
    List<BookingSelectionResponse> bookingSelectionResponseList;

}
