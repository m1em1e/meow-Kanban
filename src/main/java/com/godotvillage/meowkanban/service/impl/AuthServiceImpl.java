package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.godotvillage.meowkanban.common.exception.BaseException;
import com.godotvillage.meowkanban.common.exception.LoginFailedException;
import com.godotvillage.meowkanban.domain.entity.Role;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.domain.entity.UserRole;
import com.godotvillage.meowkanban.domain.param.LoginParam;
import com.godotvillage.meowkanban.domain.param.RegisterParam;
import com.godotvillage.meowkanban.domain.vo.LoginVO;
import com.godotvillage.meowkanban.domain.vo.UserProfileVO;
import com.godotvillage.meowkanban.mapper.RoleMapper;
import com.godotvillage.meowkanban.mapper.UserMapper;
import com.godotvillage.meowkanban.mapper.UserRoleMapper;
import com.godotvillage.meowkanban.service.IAuthService;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final String ACTIVE_STATUS = "active";
    private static final String DEFAULT_ROLE_CODE = "ROLE_USER";

    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;

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

        Role defaultRole = roleMapper.selectOne(Wrappers.<Role>lambdaQuery()
                .eq(Role::getRoleCode, DEFAULT_ROLE_CODE)
                .eq(Role::getStatus, ACTIVE_STATUS)
                .eq(Role::getDeleted, 0));
        if (defaultRole == null) {
            throw new BaseException("默认用户角色不存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(param.getPassword()));
        user.setSalt("BCrypt");
        user.setNickname(param.getNickname().trim());
        user.setEmail(email);
        user.setStatus(ACTIVE_STATUS);
        userMapper.insert(user);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(defaultRole.getId());
        userRole.setCreatedBy(user.getId());
        userRoleMapper.insert(userRole);

        return toUserProfile(user);
    }

    @Override
    public LoginVO login(LoginParam param) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(param.getUsername(), param.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            throw new LoginFailedException("用户名或密码错误");
        } catch (DisabledException e) {
            throw new LoginFailedException("账号已被禁用");
        }

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, param.getUsername())
                .eq(User::getDeleted, 0));
        if (user == null) {
            throw new LoginFailedException("用户名或密码错误");
        }

        List<String> roles = roleMapper.selectRoleCodesByUserId(user.getId());

        LoginVO loginVO = new LoginVO();
        loginVO.setUser(toUserProfile(user));
        loginVO.setRoles(roles);
        return loginVO;
    }

    private UserProfileVO toUserProfile(User user) {
        UserProfileVO profileVO = new UserProfileVO();
        profileVO.setId(user.getId());
        profileVO.setUsername(user.getUsername());
        profileVO.setNickname(user.getNickname());
        profileVO.setEmail(user.getEmail());
        profileVO.setStatus(user.getStatus());
        return profileVO;
    }
}
