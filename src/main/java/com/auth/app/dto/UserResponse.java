package com.auth.app.dto;

import com.auth.app.entity.User;
import lombok.Getter;

/**
 * Response body for GET /api/users/me.
 * Never contains password or password hash.
 */
@Getter
public class UserResponse {

    private final Long id;
    private final String username;
    private final String email;
    private final String phone;

    public UserResponse(User user) {
        this.id       = user.getId();
        this.username = user.getUsername();
        this.email    = user.getEmail();
        this.phone    = user.getPhone();
    }
}
