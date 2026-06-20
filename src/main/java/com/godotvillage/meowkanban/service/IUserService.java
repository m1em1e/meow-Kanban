package com.godotvillage.meowkanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.UserProfileUpdateParam;
import com.godotvillage.meowkanban.domain.vo.UserProfileVO;

public interface IUserService extends IService<User> {

    UserProfileVO getUserProfileVO(IdParam param);

    UserProfileVO updateUserProfileVO(UserProfileUpdateParam param);

}
