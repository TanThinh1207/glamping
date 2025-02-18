package com.group2.glamping.model.dto.response;

import com.group2.glamping.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String accessToken;
    //private String refreshToken;
    //private String expiresIn;
    private User user;
    private String message;
    private boolean isNew;

}
