package com.godotvillage.meowkanban.domain.param;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MailCaptchaSendParam {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 120, message = "邮箱长度不能超过 120 位")
    private String mail;
}
