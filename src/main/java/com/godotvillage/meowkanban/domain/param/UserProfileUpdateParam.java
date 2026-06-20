package com.godotvillage.meowkanban.domain.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileUpdateParam {

    @NotNull(message = "用户 ID 不能为空")
    private Long id;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过 50 位")
    private String nickname;

    private Integer gender;

    private LocalDate birthday;

    private Long avatarResourceId;
}
