package com.godotvillage.meowkanban.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面访问接口
 *
 * @author mkdir
 * @since 2026/06/18 13:40
 */
@Controller
public class PageController {

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

}
