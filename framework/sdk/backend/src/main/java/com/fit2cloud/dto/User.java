package com.fit2cloud.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class User {

    private String id;

    private String username;

    private String email;

    private String phone;

    private String password;

    private boolean enabled;

    public User() {
    }
}
