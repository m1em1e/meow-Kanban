package com.godotvillage.meowkanban.controller.restController;

import com.godotvillage.meowkanban.common.result.Result;
import com.godotvillage.meowkanban.common.security.JwtAuthenticationFilter;
import com.godotvillage.meowkanban.domain.param.LoginParam;
import com.godotvillage.meowkanban.domain.param.RegisterParam;
import com.godotvillage.meowkanban.domain.vo.LoginVO;
import com.godotvillage.meowkanban.domain.vo.UserProfileVO;
import com.godotvillage.meowkanban.service.IAuthService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    @Resource
    private IAuthService authService;

    @PostMapping("/register")
    public Result<UserProfileVO> register(@Valid @RequestBody RegisterParam param) {
        return Result.success(authService.register(param));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginParam param, HttpServletResponse response) {
        LoginVO loginVO = authService.login(param);
        ResponseCookie tokenCookie = ResponseCookie.from(JwtAuthenticationFilter.TOKEN_COOKIE_NAME, loginVO.getToken())
                .path("/")
                .httpOnly(true)
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
        return Result.success(loginVO);
    }
}
