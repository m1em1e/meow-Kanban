package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.godotvillage.meowkanban.common.config.EmailConfig;
import com.godotvillage.meowkanban.common.constant.ExceptionStatusCodeConstant;
import com.godotvillage.meowkanban.common.exception.BaseException;
import com.godotvillage.meowkanban.common.exception.LoginFailedException;
import com.godotvillage.meowkanban.common.security.JwtTokenProvider;
import com.godotvillage.meowkanban.domain.entity.MailCaptcha;
import com.godotvillage.meowkanban.domain.entity.Role;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.domain.entity.UserRole;
import com.godotvillage.meowkanban.domain.param.LoginParam;
import com.godotvillage.meowkanban.domain.param.MailCaptchaSendParam;
import com.godotvillage.meowkanban.domain.param.RegisterParam;
import com.godotvillage.meowkanban.domain.vo.LoginVO;
import com.godotvillage.meowkanban.domain.vo.UserProfileVO;
import com.godotvillage.meowkanban.mapper.MailCaptchaMapper;
import com.godotvillage.meowkanban.mapper.RoleMapper;
import com.godotvillage.meowkanban.mapper.UserMapper;
import com.godotvillage.meowkanban.mapper.UserRoleMapper;
import com.godotvillage.meowkanban.service.IAuthService;
import jakarta.annotation.Resource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final Integer ACTIVE_STATUS = 1;
    private static final String DEFAULT_ROLE_CODE = "ROLE_USER";
    private static final String MAIL_CAPTCHA_TEMPLATE = "email-templates/verification-code.html";
    private static final String MAIL_CAPTCHA_SUBJECT = "喵喵看板邮箱验证码";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Resource
    private UserMapper userMapper;
    @Resource
    private MailCaptchaMapper mailCaptchaMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
	@Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private JwtTokenProvider jwtTokenProvider;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private EmailConfig emailConfig;

    @Override
    @Transactional
    public UserProfileVO register(RegisterParam param) {
        String username = param.getUsername().trim();
        String email = StringUtils.hasText(param.getEmail()) ? param.getEmail().trim() : null;

        Long usernameCount = userMapper.selectCount(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username));
        if (usernameCount > 0) {
            throw new BaseException("用户名已存在");
        }

        if (email != null) {
            Long emailCount = userMapper.selectCount(Wrappers.<User>lambdaQuery()
                    .eq(User::getEmail, email));
            if (emailCount > 0) {
                throw new BaseException("邮箱已被使用");
            }
        }

		MailCaptcha captcha = mailCaptchaMapper.selectOne(Wrappers.<MailCaptcha>lambdaQuery()
				.eq(MailCaptcha::getMail, email)
				.eq(MailCaptcha::getCaptcha, param.getCaptcha().trim())
				.eq(MailCaptcha::getUsed, 0));
		if (captcha == null ||
				LocalDateTime.now().isAfter(captcha.getCreateTime().plusMinutes(MailCaptcha.EXPIRE_MINUTES))) {
			throw new BaseException("验证码失效或错误");
		}

		Role defaultRole = roleMapper.selectOne(Wrappers.<Role>lambdaQuery()
                .eq(Role::getRoleCode, DEFAULT_ROLE_CODE)
                .eq(Role::getStatus, ACTIVE_STATUS)
                .eq(Role::getDeleted, 0));
        if (defaultRole == null) {
            throw new BaseException("默认用户角色不存在");
        }

        User user = new User();
        user.setUsername(username);
		String salt = BCrypt.gensalt();
		user.setPassword(BCrypt.hashpw(param.getPassword(), salt));
		user.setSalt(salt);
        user.setNickname(param.getNickname().trim());
        user.setEmail(email);
        user.setStatus(ACTIVE_STATUS);
        userMapper.insert(user);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(defaultRole.getId());
        userRole.setCreaterId(user.getId());
        userRoleMapper.insert(userRole);

		captcha.setUsed(1);
		mailCaptchaMapper.updateById(captcha);

        return toUserProfile(user);
    }

    @Override
    public LoginVO login(LoginParam param) {
        User user;

		String loginName = param.getUsername().trim();
		if (EMAIL_PATTERN.matcher(loginName).matches()) {
			user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
					.eq(User::getEmail, loginName)
					.eq(User::getDeleted, 0));
		} else {
			user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
					.eq(User::getUsername, loginName)
					.eq(User::getDeleted, 0));
		}

        if (user == null) {
            throw new LoginFailedException("用户名或密码错误");
        }

        try {
			boolean passwordRight = BCrypt.checkpw(param.getPassword(), user.getPassword());
			if (!passwordRight) {
				throw new LoginFailedException("用户名或密码错误");
			}

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), param.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            throw new LoginFailedException("用户名或密码错误");
        } catch (DisabledException e) {
            throw new LoginFailedException("账号已被禁用");
        }

        List<String> roles = roleMapper.selectRoleCodesByUserId(user.getId());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(jwtTokenProvider.generateToken(user, roles));
        loginVO.setUser(toUserProfile(user));
        loginVO.setRoles(roles);
        return loginVO;
    }

    @Override
    @Transactional
    public void sendMailCaptcha(MailCaptchaSendParam param) {
        String mail = param.getMail().trim();
        String captcha = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        mailCaptchaMapper.update(null, Wrappers.<MailCaptcha>lambdaUpdate()
                .set(MailCaptcha::getUsed, 1)
                .eq(MailCaptcha::getMail, mail)
                .eq(MailCaptcha::getUsed, 0));

        MailCaptcha mailCaptcha = new MailCaptcha();
        mailCaptcha.setMail(mail);
        mailCaptcha.setCaptcha(captcha);
        mailCaptcha.setUsed(0);
        mailCaptcha.setCreateTime(LocalDateTime.now());
        mailCaptchaMapper.insert(mailCaptcha);

		if (!StringUtils.hasText(emailConfig.getFrom())) {
			throw new BaseException("邮件发件人未配置");
		}

		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
			helper.setFrom(new InternetAddress(
					emailConfig.getFrom(),
					StringUtils.hasText(emailConfig.getFromName()) ? emailConfig.getFromName() : emailConfig.getFrom(),
					StandardCharsets.UTF_8.name()));
			helper.setTo(mail);
			helper.setSubject(MAIL_CAPTCHA_SUBJECT);
			String template = StreamUtils.copyToString(new ClassPathResource(MAIL_CAPTCHA_TEMPLATE).getInputStream(), StandardCharsets.UTF_8)
					.replace("{{code}}", captcha)
					.replace("{{expireMinutes}}", String.valueOf(MailCaptcha.EXPIRE_MINUTES));
			helper.setText(template, true);
			javaMailSender.send(message);
		} catch (Exception e) {
			throw new BaseException("验证码邮件发送失败，请稍后重试", e,
					ExceptionStatusCodeConstant.DEFAULT_ERROR_STATUS_CODE);
		}
    }

    private UserProfileVO toUserProfile(User user) {
        UserProfileVO profileVO = new UserProfileVO();
        profileVO.setId(user.getId());
        profileVO.setUsername(user.getUsername());
        profileVO.setNickname(user.getNickname());
        profileVO.setEmail(user.getEmail());
        profileVO.setGender(user.getGender());
        profileVO.setBirthday(user.getBirthday());
        profileVO.setAvatarResourceId(user.getAvatarResourceId());
        profileVO.setJoinedTime(user.getCreatedTime());
        profileVO.setStatus(user.getStatus());
        return profileVO;
    }
}
