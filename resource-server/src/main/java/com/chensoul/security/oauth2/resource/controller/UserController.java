package com.chensoul.security.oauth2.resource.controller;

import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Comment
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">Chensoul</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping
public class UserController {
	@GetMapping("/user")
	public Principal user(Principal user) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return user;
	}
}
