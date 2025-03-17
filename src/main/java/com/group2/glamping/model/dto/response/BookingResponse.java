package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.dto.response.filter.CampSiteResponseFilter;
import com.group2.glamping.model.enums.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("dynamicFilter")
public class BookingResponse {

    int id;
    UserResponse user;
    CampSiteResponseFilter campSite;
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
    List<PaymentResponse> paymentResponseList;

}
