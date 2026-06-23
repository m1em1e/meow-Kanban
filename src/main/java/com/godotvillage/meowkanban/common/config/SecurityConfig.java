package com.godotvillage.meowkanban.common.config;

import com.godotvillage.meowkanban.common.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
						.addLogoutHandler((request, response, authentication) -> {
							SecurityContextHolder.clearContext();
							ResponseCookie tokenCookie = ResponseCookie.from(JwtAuthenticationFilter.TOKEN_COOKIE_NAME, "")
									.path("/")
									.maxAge(0)
									.sameSite("Lax")
									.build();
							response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
						})
                        .logoutSuccessHandler((request, response, authentication) -> {
							response.setStatus(200);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().write("{\"code\":1,\"msg\":\"退出成功\"}");
						}))
                .exceptionHandling(exception -> exception
						.defaultAuthenticationEntryPointFor(
								apiAuthenticationEntryPoint(),
								PathPatternRequestMatcher.withDefaults().matcher("/api/**")
						)
                        .authenticationEntryPoint(pageAuthenticationEntryPoint()))
                .authorizeHttpRequests(authorize -> authorize
							.requestMatchers(
									"/login",
									"/register",
									"/api/v1/auth/login",
									"/api/v1/auth/register",
									"/api/v1/auth/mail-captcha/send",
									"/prototype/styles.css",
									"/prototype/token.js",
									"/prototype/auth.js",
									"/prototype/register.js",
									"/favicon.ico",
									"/error"
							).permitAll()
							.requestMatchers(
									"/",
									"/boards",
									"/profile",
									"/profile/**",
									"/detail/**"
							).authenticated()
							.requestMatchers(
									"/prototype/index.html",
									"/prototype/app.js",
									"/prototype/boards.js",
									"/prototype/detail.js",
									"/prototype/profile.js"
							).authenticated()
							.requestMatchers("/api/**").authenticated()
							.anyRequest().authenticated()
                )
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public AuthenticationEntryPoint apiAuthenticationEntryPoint() {
		return (request, response, authException) -> {
			response.setStatus(401);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
		};
	}

	@Bean
	public AuthenticationEntryPoint pageAuthenticationEntryPoint() {
		return (request, response, authException) -> {
			String target = request.getRequestURI();
			if (request.getQueryString() != null) {
				target += "?" + request.getQueryString();
			}
			response.sendRedirect("/login?redirect=" + URLEncoder.encode(target, StandardCharsets.UTF_8));
		};
	}
}
