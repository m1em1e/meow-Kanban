package com.godotvillage.meowkanban.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/v1/auth/login")
                        .defaultSuccessUrl("/boards", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint()))
                .authorizeHttpRequests(authorize -> authorize
						.requestMatchers(
								"/login",
								"/register",
								"/api/v1/auth/login",
								"/api/v1/auth/register",
								"/prototype/styles.css",
								"/prototype/auth.js",
								"/prototype/register.js",
								"/prototype/boards.js",
								"/favicon.ico",
								"/error"
						).permitAll()
						.requestMatchers("/boards").authenticated()
						.requestMatchers("/api/**").authenticated()
						.anyRequest().authenticated()
                )
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
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> {
			String accept = request.getHeader("Accept");
			if (accept != null && accept.contains("application/json")) {
				response.setStatus(401);
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
				return;
			}
			response.sendRedirect("/login");
		};
	}
}
