package com.chensoul.oauth2.common.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.LoggerListener;
import org.springframework.security.core.Authentication;

/**
 * Spring Security 提供了多种方式来防止暴力密码尝试攻击。以下是一些常用的防护机制：
 * <p>
 * 1. 密码锁定（Password Lockout）：通过限制用户在一定时间内的登录尝试次数来防止暴力密码破解。在 Spring Security 中，你可以配置 AuthenticationFailureHandler 来监控登录失败事件，并在达到一定失败次数后锁定用户账户。你可以设定锁定时间，并在锁定期间阻止该用户的登录尝试。
 * <p>
 * 2. 登录延迟（Login Delay）：在用户登录失败后，增加登录尝试的时间延迟。这样做可阻止暴力攻击者通过快速尝试多个密码组合来猜测正确密码。Spring Security 中的 AuthenticationFailureHandler 可用于实现登录延迟逻辑，通过增加登录尝试的时间间隔来抵御暴力攻击。
 * <p>
 * 3. 强密码策略（Strong Password Policies）：强制用户使用强密码可以有效防止简单密码的暴力破解。Spring Security 提供了 PasswordEncoder 接口和多个实现类，如 BCryptPasswordEncoder、SCryptPasswordEncoder 等，用于对密码进行哈希和加密。同时，你可以配置密码策略要求密码包含特定字符、长度等要求。
 * <p>
 * 4. 图形验证码（CAPTCHA）：使用图形验证码可以阻止自动化的暴力密码尝试攻击。在用户登录页面中，添加图形验证码可以确保登录请求由真正的用户发起。Spring Security 提供了集成 CAPTCHA 的功能，你可以通过配置将图形验证码集成到登录流程中。
 * <p>
 * 5. IP 封禁（IP Blocking）：在检测到恶意登录尝试时，可以暂时封禁相关 IP 地址，阻止其继续尝试登录。你可以使用 Spring Security 的拦截器（Interceptor）或过滤器（Filter）来实现 IP 封禁的逻辑。
 *
 * @author chensoul
 * @since 3.0.0
 */
@Slf4j
@AllArgsConstructor
public class AuthenticationListener extends LoggerListener {
    @Override
    public void onApplicationEvent(AbstractAuthorizationEvent event) {
        super.onApplicationEvent(event);

        if (event instanceof AuthorizationFailureEvent) {
            onAuthorizationFailureEvent((AuthorizationFailureEvent) event);
        }
    }

    /**
     * @param event
     */
    public void onAuthorizationFailureEvent(AuthorizationFailureEvent event) {
        Authentication authentication = event.getAuthentication();

        log.warn("{} 登录失败: {}", authentication.getName(), event.getAccessDeniedException().getMessage());
    }
}
