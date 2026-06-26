package com.godotvillage.meowkanban.controller.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.godotvillage.meowkanban.domain.entity.MailCaptcha;
import com.godotvillage.meowkanban.mapper.MailCaptchaMapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author mkdir
 * @since 2026/06/26 09:39
 */
@Component
public class CaptchaClearTask {

	@Resource
	private MailCaptchaMapper mailCaptchaMapper;

	@Scheduled(cron = "0 0 0 * * *")
	public void captchaClear() {
		mailCaptchaMapper.delete(Wrappers.<MailCaptcha>lambdaQuery()
				.le(MailCaptcha::getCreateTime, LocalDateTime.now().minusMinutes(MailCaptcha.EXPIRE_MINUTES))
				.or()
				.eq(MailCaptcha::getUsed, 1)
		);
	}

}
