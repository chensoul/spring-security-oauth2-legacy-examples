package com.chensoul.security.oauth2.common.support;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

import java.util.Map;

/**
 *
 */
public class CustomAuthenticationKeyGenerator extends DefaultAuthenticationKeyGenerator {
    /**
     * @param values
     * @return
     */
    @Override
    protected String generateKey(Map<String, String> values) {
        return super.generateKey(values) + RandomStringUtils.randomAlphabetic(8);
    }
}
