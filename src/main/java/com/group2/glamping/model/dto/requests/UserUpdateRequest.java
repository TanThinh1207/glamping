package com.group2.glamping.model.dto.requests;

import java.time.LocalDate;
import java.util.Date;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String phone,
        Date dob,
        Boolean status
) {
}
