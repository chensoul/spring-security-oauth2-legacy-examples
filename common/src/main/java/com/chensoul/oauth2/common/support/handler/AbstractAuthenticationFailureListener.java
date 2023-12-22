package com.chensoul.oauth2.common.support.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * <p>
 * 认证失败事件处理器
 * </p>
 */
@Slf4j
public abstract class AbstractAuthenticationFailureListener
    implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    /**
     * Handle an application service.
     *
     * @param event the service to respond to
     */
    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        AuthenticationException authenticationException = event.getException();
        Authentication authentication = (Authentication) event.getSource();

        log.warn("{} 登录失败: {}", event.getAuthentication().getName(), event.getException().getMessage());

        handle(authenticationException, authentication);
    }

    /**
     * 处理登录成功方法
     * <p>
     *
     * @param authenticationException 登录的authentication 对象
     * @param authentication          登录的authenticationException 对象
     */
    public abstract void handle(AuthenticationException authenticationException, Authentication authentication);

}
