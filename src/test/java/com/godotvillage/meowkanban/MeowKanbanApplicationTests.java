package com.godotvillage.meowkanban;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.entity.MailCaptcha;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.domain.param.BoardInfoQueryParam;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.LoginParam;
import com.godotvillage.meowkanban.domain.param.RegisterParam;
import com.godotvillage.meowkanban.domain.param.UserProfileUpdateParam;
import com.godotvillage.meowkanban.domain.vo.BoardInfoVO;
import com.godotvillage.meowkanban.domain.vo.FileResourceContentVO;
import com.godotvillage.meowkanban.domain.vo.FileResourceInfoVO;
import com.godotvillage.meowkanban.domain.vo.LoginVO;
import com.godotvillage.meowkanban.domain.vo.UserProfileVO;
import com.godotvillage.meowkanban.mapper.MailCaptchaMapper;
import com.godotvillage.meowkanban.mapper.UserMapper;
import com.godotvillage.meowkanban.service.IAuthService;
import com.godotvillage.meowkanban.service.IBoardService;
import com.godotvillage.meowkanban.service.IFileResourceService;
import com.godotvillage.meowkanban.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "meow-kanban.resource.storage-dir=target/test-resource-files")
class MeowKanbanApplicationTests {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private IAuthService authService;
    @Autowired
    private IBoardService boardService;
    @Autowired
    private IFileResourceService fileResourceService;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailCaptchaMapper mailCaptchaMapper;
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
    void listBoardInfoUsesClientPagination() {
        BoardInfoQueryParam param = new BoardInfoQueryParam();
        param.setPageIndex(1);
        param.setPageSize(10);

        PageResult<BoardInfoVO> result = boardService.listBoardInfo(param);

        assertNotNull(result.getRecords());
        assertEquals(1L, result.getPageIndex());
        assertEquals(10L, result.getPageSize());
        assertTrue(result.getRecords().size() <= 10);
    }

    @Test
    void listRecentParticipatedBoardsLimitsResult() {
        IdParam param = new IdParam();
        param.setId(1L);

        java.util.List<BoardInfoVO> result = boardService.listRecentParticipatedBoards(param);

        assertNotNull(result);
        assertTrue(result.size() <= 5);
        assertTrue(result.stream().allMatch(board -> board.getOwnerId() != null));
    }

    @Test
    void uploadAndLoadFileResource() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                "text/plain",
                "hello resource".getBytes()
        );

        FileResourceInfoVO info = fileResourceService.upload(file);
        FileResourceContentVO content = fileResourceService.loadContent(info.getId());

        assertEquals("hello.txt", info.getFileName());
        assertEquals("text/plain", content.getContentType());
        assertEquals("hello resource", new String(content.getResource().getInputStream().readAllBytes()));
    }

    @Test
    @Transactional
    void mailCaptchaPersistsAndExpiresInFiveMinutes() {
        LocalDateTime createTime = LocalDateTime.now().minusMinutes(4);
        MailCaptcha mailCaptcha = new MailCaptcha();
        mailCaptcha.setMail("verify-" + System.nanoTime() + "@meowkanban.local");
        mailCaptcha.setCaptcha("123456");
        mailCaptcha.setUsed(0);
        mailCaptcha.setCreateTime(createTime);

        mailCaptchaMapper.insert(mailCaptcha);

        MailCaptcha saved = mailCaptchaMapper.selectById(mailCaptcha.getId());
        assertNotNull(saved);
        assertEquals("123456", saved.getCaptcha());
        assertTrue(saved.isAvailable(createTime.plusMinutes(4).plusSeconds(59)));
        assertTrue(saved.isExpired(createTime.plusMinutes(MailCaptcha.EXPIRE_MINUTES)));

        saved.setUsed(1);
        assertFalse(saved.isAvailable(createTime.plusMinutes(1)));
    }

    @Test
    @Transactional
    void getAndUpdateUserProfile() {
        IdParam idParam = new IdParam();
        idParam.setId(1L);

        UserProfileVO profile = userService.getUserProfileVO(idParam);
        UserProfileUpdateParam updateParam = new UserProfileUpdateParam();
        updateParam.setId(profile.getId());
        updateParam.setNickname("管理员测试");
        updateParam.setGender(1);
        updateParam.setBirthday(LocalDate.of(1990, 1, 1));

        UserProfileVO updatedProfile = userService.updateUserProfileVO(updateParam);

        assertEquals(profile.getUsername(), updatedProfile.getUsername());
        assertEquals(profile.getEmail(), updatedProfile.getEmail());
        assertEquals("管理员测试", updatedProfile.getNickname());
        assertEquals(1, updatedProfile.getGender());
        assertEquals(LocalDate.of(1990, 1, 1), updatedProfile.getBirthday());
    }

    @Test
    @Transactional
    void registerUsesBCryptPasswordAndLoginWorks() {
        String username = "test_user_" + System.nanoTime();
        String password = "secret123";
        String email = username + "@meowkanban.local";
        String captchaCode = "654321";

        MailCaptcha mailCaptcha = new MailCaptcha();
        mailCaptcha.setMail(email);
        mailCaptcha.setCaptcha(captchaCode);
        mailCaptcha.setUsed(0);
        mailCaptcha.setCreateTime(java.time.LocalDateTime.now());
        mailCaptchaMapper.insert(mailCaptcha);

        RegisterParam registerParam = new RegisterParam();
        registerParam.setUsername(username);
        registerParam.setNickname("测试用户");
        registerParam.setEmail(email);
        registerParam.setCaptcha(captchaCode);
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

        LoginParam emailLoginParam = new LoginParam();
        emailLoginParam.setUsername(email);
        emailLoginParam.setPassword(password);

        LoginVO emailLoginVO = authService.login(emailLoginParam);
        assertEquals(user.getId(), emailLoginVO.getUser().getId());
        assertTrue(emailLoginVO.getRoles().contains("ROLE_USER"));
    }
}
