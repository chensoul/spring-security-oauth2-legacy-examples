package com.chensoul.oauth2.common.support.handler;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;

/**
 * <p>
 * 退出成功事件处理器
 * </p>
 */
public abstract class AbstractLogoutSuccessListener implements ApplicationListener<LogoutSuccessEvent> {

    /**
     * Handle an application service.
     *
     * @param event the service to respond to
     */
    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        Authentication authentication = (Authentication) event.getSource();
        if (!CollectionUtils.isEmpty(authentication.getAuthorities())) {
            handle(authentication);
        }
    }

    /**
     * 处理退出成功方法
     * <p>
     * 获取到登录的authentication 对象
     *
     * @param authentication 登录对象
     */
    public abstract void handle(Authentication authentication);

}
