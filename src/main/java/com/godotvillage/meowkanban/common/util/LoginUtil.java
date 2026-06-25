package com.godotvillage.meowkanban.common.util;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class LoginUtil {

    private static UserMapper userMapper;

    public LoginUtil(UserMapper userMapper) {
        LoginUtil.userMapper = userMapper;
    }

    public static Long getLoginId() {
        String username = getLoginUsername();
        if (!StringUtils.hasText(username) || userMapper == null) {
            return null;
        }

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .select(User::getId)
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
        return user == null ? null : user.getId();
    }

    private static String getLoginUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        if (!StringUtils.hasText(username) || "anonymousUser".equals(username)) {
            return null;
        }
        return username;
    }
}
