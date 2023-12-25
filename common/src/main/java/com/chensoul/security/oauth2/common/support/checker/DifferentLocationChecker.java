package com.chensoul.security.oauth2.common.support.checker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

/**
 * ip 地址检测
 * <p>
 *
 * @author chensoul
 * @since 3.0.0
 */
@Slf4j
@Component
public class DifferentLocationChecker implements UserDetailsChecker {
    /**
     * @param userDetails the UserDetails instance whose status should be checked.
     */
    @Override
    public void check(UserDetails userDetails) {
//        final String ip = WebUtils.getClientIp(WebUtils.getRequest().orElse(null));
//        log.info("username: {}, ip: {}", userDetails.getUsername(), ip);
//        final NewLocationToken token = userService.isNewLoginLocation(userDetails.getUsername(), ip);
//        if (token != null) {
//            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
//            eventPublisher.publishEvent(new OnDifferentLocationLoginEvent(request.getLocale(), userDetails.getUsername(), ip, token, appUrl));
//            throw new UnusualLocationException("unusual checker");
//        }
    }
}
