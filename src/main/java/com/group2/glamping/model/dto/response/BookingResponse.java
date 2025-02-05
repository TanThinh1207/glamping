package com.group2.glamping.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookingResponse {

    int statusCode;
    String message;
    Object data;

}
