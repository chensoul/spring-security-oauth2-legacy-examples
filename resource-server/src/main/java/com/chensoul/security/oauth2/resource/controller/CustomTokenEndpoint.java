//package com.chensoul.oauth2.resource.controller;
//
//import lombok.AllArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@FrameworkEndpoint
//@AllArgsConstructor
//public class CustomTokenEndpoint {
//	private DefaultTokenServices defaultTokenServices;
//
//	@DeleteMapping("/oauth/token")
//	@ResponseBody
//	public void revokeToken(HttpServletRequest request) {
//		String authorization = request.getHeader("Authorization");
//		if (authorization != null && authorization.contains("Bearer")) {
//			String tokenId = authorization.substring("Bearer".length() + 1);
//			defaultTokenServices.revokeToken(tokenId);
//		}
//	}
//}
