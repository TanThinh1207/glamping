package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private int id;
    private String email;
    private String firstname;
    private String lastname;
    private String address;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private boolean status;
    private List<Integer> campSiteIds;


    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.address = user.getAddress();
        this.phone = user.getPhoneNumber();
        this.birthday = user.getDob();
        this.status = user.isStatus();
        this.campSiteIds = user.getCampSiteList().stream()
                .map(CampSite::getId)
                .collect(Collectors.toList());
    }
}
