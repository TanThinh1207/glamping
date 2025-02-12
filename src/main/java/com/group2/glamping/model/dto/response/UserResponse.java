package com.group2.glamping.model.dto.response;

import com.group2.glamping.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;

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
    private LocalDate birthday;
    private boolean status;


    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.address = user.getAddress();
        this.phone = user.getPhoneNumber();
        this.birthday = user.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.status = user.isStatus();
    }
}
