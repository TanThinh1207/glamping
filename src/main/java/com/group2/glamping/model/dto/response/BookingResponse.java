package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("dynamicFilter")
public class BookingResponse {

    int id;
    UserResponse user;
    CampSiteResponse campSite;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDateTime checkIn;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDateTime checkOut;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    BookingStatus status;
    double totalAmount;
    List<BookingDetailResponse> bookingDetailResponseList;
    List<BookingSelectionResponse> bookingSelectionResponseList;
    String comment;
}
