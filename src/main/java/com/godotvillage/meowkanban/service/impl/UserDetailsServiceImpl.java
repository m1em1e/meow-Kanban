package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.mapper.RoleMapper;
import com.godotvillage.meowkanban.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Integer ACTIVE_STATUS = 1;

	@Resource
    private UserMapper userMapper;
	@Resource
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        List<String> roleCodes = roleMapper.selectRoleCodesByUserId(user.getId());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(roleCodes.toArray(String[]::new))
                .disabled(!ACTIVE_STATUS.equals(user.getStatus()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}
