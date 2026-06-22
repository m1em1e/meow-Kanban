package com.godotvillage.meowkanban.service;

import com.godotvillage.meowkanban.domain.param.LoginParam;
import com.godotvillage.meowkanban.domain.param.MailCaptchaSendParam;
import com.godotvillage.meowkanban.domain.param.RegisterParam;
import com.godotvillage.meowkanban.domain.vo.LoginVO;
import com.godotvillage.meowkanban.domain.vo.UserProfileVO;

public interface IAuthService {

    UserProfileVO register(RegisterParam param);

    LoginVO login(LoginParam param);

    void sendMailCaptcha(MailCaptchaSendParam param);
}
