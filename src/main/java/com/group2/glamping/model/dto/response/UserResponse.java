package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.model.enums.Role;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonFilter("dynamicFilter")
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private Role role;
    private List<Integer> campSiteIds;
    private String connectionId;
    private boolean isRestricted;


    public UserResponse(User user) throws StripeException {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.address = user.getAddress();
        this.phone = user.getPhoneNumber();
        this.birthday = user.getDob();
        this.status = user.isStatus();
        this.role = user.getRole();
        this.campSiteIds = user.getCampSiteList() == null ? new ArrayList<>() : user.getCampSiteList().stream()
                .map(CampSite::getId)
                .collect(Collectors.toList());
        this.connectionId = user.getConnectionId();
        this.isRestricted = (connectionId != null) && isAccountRestricted(connectionId);

    }

    public boolean isAccountRestricted(String accountId) throws StripeException {
        Account account = Account.retrieve(accountId);
        return !account.getRequirements().getCurrentlyDue().isEmpty();
    }
}
