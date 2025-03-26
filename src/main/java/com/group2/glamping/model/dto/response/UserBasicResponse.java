package com.group2.glamping.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicResponse {
    private int id;
    private String email;
    private String firstname;
    private String lastname;
}
