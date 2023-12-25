package com.chensoul.security.oauth2.authorization.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final AccessDeniedHandler accessDeniedHandler;
	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//前后端分离项目，不需要session
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
			.and()
			.authorizeRequests().mvcMatchers("/error", "/oauth/**").permitAll()
			.and()
			// 所有请求需要认证才能被访问  fullyAuthenticated：禁用anonymous和rememberMe
			.authorizeRequests().anyRequest().authenticated()
			// 允许表单登录
			.and().formLogin()
			// 设置退出
			.and().logout()
			.and().headers().frameOptions().disable()
			.and().httpBasic()
			.and().csrf().disable()
			// 异常处理
			.exceptionHandling()
			// 定义权限不足处理器
			.accessDeniedHandler(accessDeniedHandler);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/favicon.ico");
	}
}
