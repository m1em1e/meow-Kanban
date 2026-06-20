package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private Integer gender;

    private LocalDate birthday;

    private String status;
}
