package com.group2.glamping.model.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CampTypeItemResponse {

    BookingDetailResponse bookingDetail;
    int quantity;
    Double total;


}
