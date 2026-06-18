package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;

@Data
public class UserProfileVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String status;
}
