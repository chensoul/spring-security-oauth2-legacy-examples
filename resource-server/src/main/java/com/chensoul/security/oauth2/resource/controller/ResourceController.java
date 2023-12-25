package com.chensoul.security.oauth2.resource.controller;

import com.chensoul.security.oauth2.common.annotation.Inner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ResourceController {

	@Inner
	@GetMapping("/public")
	public String common() {
		return "Public Resource!";
	}

	@GetMapping("/resource")
	public String resource() {
		return "Secured Web Resource!";
	}
}
