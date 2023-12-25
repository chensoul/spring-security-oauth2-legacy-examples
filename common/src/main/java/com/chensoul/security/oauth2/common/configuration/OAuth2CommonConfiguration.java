package com.chensoul.security.oauth2.common.configuration;

import com.chensoul.security.oauth2.common.event.AuthenticationListener;
import com.chensoul.security.oauth2.common.event.AuthenticationSuccessListener;
import com.chensoul.security.oauth2.common.event.LogoutSuccessListener;
import com.chensoul.security.oauth2.common.feign.OAuth2FeignRequestInterceptor;
import com.chensoul.security.oauth2.common.support.CustomWebResponseExceptionTranslator;
import com.chensoul.security.oauth2.common.support.PermissionService;
import com.chensoul.security.oauth2.common.support.RestResponseMessageResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.event.LoggerListener;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.Servlet;

/**
 *
 */
@Configuration
@ConditionalOnClass(Servlet.class)
public class OAuth2CommonConfiguration {

	/**
	 * web响应异常转换器
	 */
	@Bean
	public WebResponseExceptionTranslator webResponseExceptionTranslator() {
		return new CustomWebResponseExceptionTranslator();
	}

	/**
	 * OAuth2 AccessDeniedHandler
	 */
	@Bean
	public AccessDeniedHandler accessDeniedHandler(WebResponseExceptionTranslator exceptionTranslator) {
		OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
		accessDeniedHandler.setExceptionTranslator(exceptionTranslator);
		return accessDeniedHandler;
	}

	/**
	 * OAuth2 AuthenticationEntryPoint
	 */
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint(WebResponseExceptionTranslator exceptionTranslator) {
		OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		authenticationEntryPoint.setExceptionTranslator(exceptionTranslator);
		return authenticationEntryPoint;
	}

	/**
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/**
	 * @return
	 */
	@Bean
	public RestResponseMessageResolver restResponseMessageResolver() {
		return new RestResponseMessageResolver();
	}

	/**
	 * @return
	 */
	@Bean("pms")
	public PermissionService permissionService() {
		return new PermissionService();
	}

	/**
	 * @return
	 */
	@Bean
	public AuthenticationSuccessListener authenticationSuccessListener() {
		return new AuthenticationSuccessListener();
	}

	/**
	 * @return
	 */
	@Bean
	public AuthenticationListener authenticationListener() {
		return new AuthenticationListener();
	}

	/**
	 * @return
	 */
	@Bean
	public LogoutSuccessListener logoutSuccessListener() {
		return new LogoutSuccessListener();
	}

	/**
	 * @return
	 */
	@Bean
	public LoggerListener loggerListener() {
		return new LoggerListener();
	}

	/**
	 * @return
	 */
	@Bean
	public org.springframework.security.authentication.event.LoggerListener oauthLoggerListener() {
		return new org.springframework.security.authentication.event.LoggerListener();
	}

	/**
	 * @return
	 */
	@Bean
	public OAuth2FeignRequestInterceptor feignOAuth2RequestInterceptor() {
		return new OAuth2FeignRequestInterceptor();
	}
}
