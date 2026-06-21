package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.common.exception.BaseException;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.UserProfileUpdateParam;
import com.godotvillage.meowkanban.domain.vo.UserProfileVO;
import com.godotvillage.meowkanban.mapper.UserMapper;
import com.godotvillage.meowkanban.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public UserProfileVO getUserProfileVO(IdParam param) {
        User user = param == null || param.getId() == null
                ? getCurrentUser()
                : baseMapper.selectById(param.getId());
        if (user == null) {
            throw new BaseException("用户不存在");
        }

        return toUserProfileVO(user);
    }

    @Override
    @Transactional
    public UserProfileVO updateUserProfileVO(UserProfileUpdateParam param) {
        User user = baseMapper.selectById(param.getId());
        if (user == null) {
            throw new BaseException("用户不存在");
        }

        validateGender(param.getGender());
        user.setNickname(param.getNickname().trim());
        user.setGender(param.getGender() == null ? -1 : param.getGender());
        user.setBirthday(param.getBirthday());
        if (param.getAvatarResourceId() != null) {
            user.setAvatarResourceId(param.getAvatarResourceId());
        }
        updateById(user);

        return toUserProfileVO(user);
    }

    private void validateGender(Integer gender) {
        if (gender != null && gender != -1 && gender != 0 && gender != 1) {
            throw new BaseException("性别值不合法");
        }
    }

    private UserProfileVO toUserProfileVO(User user) {
        UserProfileVO userProfileVO = new UserProfileVO();
        BeanUtils.copyProperties(user, userProfileVO);
        userProfileVO.setJoinedTime(user.getCreatedTime());
        return userProfileVO;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        if (!StringUtils.hasText(username) || "anonymousUser".equals(username)) {
            return null;
        }

        return lambdaQuery()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0)
                .one();
    }

}
