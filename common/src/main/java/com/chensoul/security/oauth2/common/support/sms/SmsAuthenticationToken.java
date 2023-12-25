package com.chensoul.security.oauth2.common.support.sms;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * <p>
 *
 * @author chensoul
 * @since 3.0.0
 */
public class SmsAuthenticationToken extends UsernamePasswordAuthenticationToken {
    /**
     * @param principal
     * @param credentials
     * @param grantType
     */
    public SmsAuthenticationToken(Object principal, Object credentials, String grantType) {
        super(principal, credentials);
        this.grantType = grantType;
    }

    /**
     * @param principal
     * @param credentials
     * @param authorities
     * @param grantType
     */
    public SmsAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String grantType) {
        super(principal, credentials, authorities);
        this.grantType = grantType;
    }

    /**
     * 授权类型
     */
    @Getter
    private String grantType;

}
