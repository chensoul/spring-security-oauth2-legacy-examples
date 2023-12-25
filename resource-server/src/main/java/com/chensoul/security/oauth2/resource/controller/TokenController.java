//package com.chensoul.oauth2.resource.controller;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Collection;
//
//@Slf4j
//@RestController
//@AllArgsConstructor
//public class TokenController {
//
//	private DefaultTokenServices defaultTokenServices;
//	private TokenStore tokenStore;
//
//	@GetMapping("/oauth/token")
//	@ResponseBody
//	public Collection<OAuth2AccessToken> getTokens(@RequestParam(required = false) String clientId,
//												   @RequestParam(required = false) String username, HttpServletRequest request) {
//		if (StringUtils.isBlank(clientId)) {
//			clientId = ((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getOAuth2Request().getClientId();
//		}
//		Collection<OAuth2AccessToken> tokens;
//		if (StringUtils.isNotBlank(username)) {
//			tokens = tokenStore.findTokensByClientIdAndUserName(clientId, username);
//		} else {
//			tokens = tokenStore.findTokensByClientId(clientId);
//		}
//		return tokens;
//	}
//
//	@PutMapping("/oauth/token/{tokenId:.*}")
//	@ResponseBody
//	public String removeRefreshToken(@PathVariable String tokenId) {
//		if (tokenStore instanceof JdbcTokenStore) {
//			((JdbcTokenStore) tokenStore).removeRefreshToken(tokenId);
//		}
//		return tokenId;
//	}
//
//	@DeleteMapping("/oauth/token/{tokenId}")
//	@ResponseBody
//	public void revokeToken(HttpServletRequest request, @PathVariable String tokenId) {
//		defaultTokenServices.revokeToken(tokenId);
//	}
//}
