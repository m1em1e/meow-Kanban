package com.godotvillage.meowkanban.controller.restController;

import com.godotvillage.meowkanban.common.result.Result;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.UserProfileUpdateParam;
import com.godotvillage.meowkanban.domain.vo.UserProfileVO;
import com.godotvillage.meowkanban.service.IUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    @Resource
    private IUserService userService;

    @GetMapping("/profile")
    public Result<UserProfileVO> getUserProfileVO(IdParam param) {
        return Result.success(userService.getUserProfileVO(param));
    }

    @PutMapping("/profile")
    public Result<UserProfileVO> updateUserProfileVO(@Valid @RequestBody UserProfileUpdateParam param) {
        return Result.success(userService.updateUserProfileVO(param));
    }
}
