package com.group2.glamping.auth.google;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GoogleCallbackConfig {

    @Value("${google.callback.url}")
    private String googleCallbackUrl;

    @PostConstruct
    public void init() {
        System.out.println("Google Callback URL: " + googleCallbackUrl);
    }

}
