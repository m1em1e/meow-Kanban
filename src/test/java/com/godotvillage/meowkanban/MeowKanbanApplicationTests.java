package com.godotvillage.meowkanban;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.domain.param.LoginParam;
import com.godotvillage.meowkanban.domain.param.RegisterParam;
import com.godotvillage.meowkanban.domain.vo.LoginVO;
import com.godotvillage.meowkanban.mapper.UserMapper;
import com.godotvillage.meowkanban.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MeowKanbanApplicationTests {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

    @Test
    void loadDefaultAdminUser() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
    }

    @Test
    @Transactional
    void registerUsesBCryptPasswordAndLoginWorks() {
        String username = "test_user_" + System.nanoTime();
        String password = "secret123";

        RegisterParam registerParam = new RegisterParam();
        registerParam.setUsername(username);
        registerParam.setNickname("测试用户");
        registerParam.setEmail(username + "@meowkanban.local");
        registerParam.setPassword(password);

        authService.register(registerParam);

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username));
        assertNotEquals(password, user.getPassword());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));

        LoginParam loginParam = new LoginParam();
        loginParam.setUsername(username);
        loginParam.setPassword(password);

        LoginVO loginVO = authService.login(loginParam);
        assertTrue(loginVO.getRoles().contains("ROLE_USER"));
    }
}
