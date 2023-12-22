package com.chensoul.oauth2.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * TODO Comment
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">Chensoul</a>
 * @since TODO
 */
@RestController
@RequestMapping
public class UserController {
	@GetMapping("/userinfo")
	public Principal user(Principal user) {
		return user;
	}
}
