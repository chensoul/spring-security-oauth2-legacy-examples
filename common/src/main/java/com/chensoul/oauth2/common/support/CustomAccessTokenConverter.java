package com.chensoul.oauth2.common.support;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Map;

/**
 *
 */
public class CustomAccessTokenConverter extends DefaultAccessTokenConverter {

    /**
     * @param claims information decoded from an access token
     * @return
     */
    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
        OAuth2Authentication authentication = super.extractAuthentication(claims);
        authentication.setDetails(claims);
        return authentication;
    }
}
