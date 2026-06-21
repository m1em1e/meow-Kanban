package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginVO {

    private String token;

    private UserProfileVO user;

    private List<String> roles;
}
