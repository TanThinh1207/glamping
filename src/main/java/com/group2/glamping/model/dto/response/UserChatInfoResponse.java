package com.group2.glamping.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChatInfoResponse {
        private int id;
        private String firstname;
        private String email;

        public UserChatInfoResponse(UserResponse userResponse) {
            this.id = userResponse.getId();
            this.firstname = userResponse.getFirstname();
            this.email = userResponse.getEmail();
    }
}
