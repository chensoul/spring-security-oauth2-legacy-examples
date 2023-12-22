package com.chensoul.oauth2.common.support.handler;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;

/**
 * <p>
 * 认证成功事件处理器
 * </p>
 */
public abstract class AbstractAuthenticationSuccessListener
    implements ApplicationListener<AuthenticationSuccessEvent> {

    /**
     * Handle an application service.
     *
     * @param event the service to respond to
     */
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = (Authentication) event.getSource();
        if (!CollectionUtils.isEmpty(authentication.getAuthorities())) {
            handle(authentication);
        }
    }

    /**
     * 处理登录成功方法
     * <p>
     * 获取到登录的authentication 对象
     *
     * @param authentication 登录对象
     */
    public abstract void handle(Authentication authentication);

}
