package com.godotvillage.meowkanban.domain.param;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterParam {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须为 3-50 位")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "用户名仅支持字母、数字、下划线或短横线")
    private String username;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过 50 位")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Size(max = 120, message = "邮箱长度不能超过 120 位")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 72, message = "密码长度必须为 6-72 位")
    private String password;
}
