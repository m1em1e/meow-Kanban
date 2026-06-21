package com.godotvillage.meowkanban.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 页面访问接口
 *
 * @author mkdir
 * @since 2026/06/18 13:40
 */
@Controller
public class PageController {

	private final UserMapper userMapper;

	public PageController(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@GetMapping("/")
	public String index() {
		return "redirect:/boards";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/boards")
	public String boards() {
		return "boards";
	}

	@GetMapping("/profile")
	public String profile(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return "redirect:/login";
		}
		User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
				.eq(User::getUsername, authentication.getName())
				.eq(User::getDeleted, 0));
		if (user == null) {
			return "redirect:/login";
		}
		return "redirect:/profile/" + user.getId();
	}

	@GetMapping("/profile/{userId}")
	public String profile(@PathVariable Long userId, Authentication authentication, Model model) {
		model.addAttribute("userId", userId);
		if (authentication != null && authentication.isAuthenticated()) {
			User currentUser = userMapper.selectOne(Wrappers.<User>lambdaQuery()
					.eq(User::getUsername, authentication.getName())
					.eq(User::getDeleted, 0));
			if (currentUser != null) {
				model.addAttribute("currentUserId", currentUser.getId());
			}
		}
		return "profile";
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("boardId", id);
		return "detail";
	}

}
