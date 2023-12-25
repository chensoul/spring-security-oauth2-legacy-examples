package com.chensoul.security.oauth2.common.support;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;

import java.util.Map;

/**
 *
 */
public class CustomJwtClaimsSetVerifier implements JwtClaimsSetVerifier {

    /**
     * @param claims the JWT Claims Set
     * @throws InvalidTokenException
     */
    @Override
    public void verify(Map<String, Object> claims) throws InvalidTokenException {
        final String username = (String) claims.get("client_id");
        if (StringUtils.isBlank(username)) {
            throw new InvalidTokenException("token中客户的ID不能为空");
        }
    }

}
