package com.godotvillage.meowkanban.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mk_mail_captcha")
public class MailCaptcha {

    public static final long EXPIRE_MINUTES = 5L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String mail;

    private String captcha;

    private Integer used;

    @TableField("create_time")
    private LocalDateTime createTime;

    public boolean isExpired(LocalDateTime now) {
        if (createTime == null) {
            return true;
        }
        return !createTime.plusMinutes(EXPIRE_MINUTES).isAfter(now);
    }

    public boolean isAvailable(LocalDateTime now) {
        return Integer.valueOf(0).equals(used) && !isExpired(now);
    }
}
