package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserProfileVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private Integer gender;

    private LocalDate birthday;

    private Long avatarResourceId;

    private LocalDateTime joinedTime;

    private Integer status;
}
