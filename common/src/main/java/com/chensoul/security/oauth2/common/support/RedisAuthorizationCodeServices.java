package com.chensoul.security.oauth2.common.support;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

/**
 *
 */
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    /**
     * redis template
     */
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * auth code
     */
    private static final String AUTH_CODE = "auth_code:";
    /**
     * expired seconds
     */
    private long expiredSeconds = 300L;
    /**
     * prefix
     */
    private String prefix = "";

    /**
     * @param redisTemplate redis template
     */
    public RedisAuthorizationCodeServices(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * @param code           code
     * @param authentication authentication
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        redisTemplate.opsForValue().set(prefix + AUTH_CODE + code, authentication, expiredSeconds);
    }

    /**
     * @param code
     * @return
     */
    @Override
    protected OAuth2Authentication remove(String code) {
        return (OAuth2Authentication) redisTemplate.opsForValue().getAndDelete(prefix + code);
    }

    /**
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @param expiredSeconds
     */
    public void setExpiredSeconds(long expiredSeconds) {
        this.expiredSeconds = expiredSeconds;
    }
}
