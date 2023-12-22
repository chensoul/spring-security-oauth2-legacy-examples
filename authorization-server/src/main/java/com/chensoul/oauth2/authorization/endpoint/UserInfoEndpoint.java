package com.chensoul.oauth2.authorization.endpoint;

import com.chensoul.oauth2.common.model.RestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/userinfo")
public class UserInfoEndpoint {

    @GetMapping
    public RestResponse<Principal> userinfo(Principal principal){
        return RestResponse.ok(principal);
    }
}
