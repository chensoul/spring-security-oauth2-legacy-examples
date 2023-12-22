package com.chensoul.oauth2.common.support.sms;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 *
 * @author chensoul
 * @since 3.0.0
 */
public class SmsTokenGranter extends AbstractTokenGranter {
    private final AuthenticationManager authenticationManager;

    /**
     * @param authenticationManager
     * @param tokenServices
     * @param clientDetailsService
     * @param requestFactory
     */
    public SmsTokenGranter(AuthenticationManager authenticationManager,
                           AuthorizationServerTokenServices tokenServices,
                           ClientDetailsService clientDetailsService,
                           OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, "sms");
        this.authenticationManager = authenticationManager;

    }

    /**
     * @param client
     * @param tokenRequest
     * @return
     */
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());

        String phone = parameters.get("phone");
        String code = parameters.get("code");

        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            throw new BadCredentialsException("手机号和验证码不能为空");
        }

        // Protect from downstream leaks of code
        parameters.remove("code");

        Authentication authentication = new SmsAuthenticationToken(phone, code, tokenRequest.getGrantType());
        ((AbstractAuthenticationToken) authentication).setDetails(parameters);
        try {
            authentication = authenticationManager.authenticate(authentication);
        } catch (AccountStatusException | BadCredentialsException ase) {
            throw new InvalidGrantException(ase.getMessage());
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + phone);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, authentication);
    }

}
