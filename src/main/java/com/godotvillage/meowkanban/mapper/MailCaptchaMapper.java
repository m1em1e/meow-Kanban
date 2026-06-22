package com.godotvillage.meowkanban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.godotvillage.meowkanban.domain.entity.MailCaptcha;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailCaptchaMapper extends BaseMapper<MailCaptcha> {
}
