package com.group2.glamping.model.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyTokenRequest {
    private String idToken;
}
