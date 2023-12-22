package com.chensoul.oauth2.common.util;

import com.chensoul.oauth2.common.model.LoggedUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *
 * @author chensoul
 * @since 3.0.0
 */
public final class SecurityUtils {
    /**
     *
     */
    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication.getPrincipal() instanceof LoggedUser) {
            return ((LoggedUser) authentication.getPrincipal()).getName();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && getAuthorities(authentication).noneMatch("ROLE_ANONYMOUS"::equals);
    }

    /**
     * Checks if the current user has any of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has any of the authorities, false otherwise.
     */
    public static boolean hasAnyOfAuthorities(String... authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (
            authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority))
        );
    }

    /**
     * Checks if the current user has none of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has none of the authorities, false otherwise.
     */
    public static boolean hasNoneOfAuthorities(String... authorities) {
        return !hasAnyOfAuthorities(authorities);
    }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    public static boolean hasAuthority(String authority) {
        return hasAnyOfAuthorities(authority);
    }

    /**
     * @param authentication
     * @return
     */
    private static Stream<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    }

    /**
     * @return
     */
    public static Set<String> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return getAuthorities(authentication).collect(Collectors.toSet());
    }

    /**
     * @param authentication
     * @return
     */
    public static LoggedUser getUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof LoggedUser) {
            return (LoggedUser) authentication.getPrincipal();
        } else if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
            return (LoggedUser) auth2Authentication.getPrincipal();
        }
        return null;
    }

    /**
     * @return
     */
    public static LoggedUser getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUser(authentication);
    }

    /**
     * @return
     */
    public static Map<String, Object> getAuthenticationDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return new HashMap<>();
        }

        Map<String, Object> decodedDetails = null;
        Object details = authentication.getDetails();
        if (details != null && details instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) authentication.getDetails();
            decodedDetails = (Map<String, Object>) oAuth2AuthenticationDetails.getDecodedDetails();
        }

        if (decodedDetails == null) {
            decodedDetails = new HashMap<>();
        }

        return decodedDetails;
    }

    /**
     * @param key
     * @return
     */
    public static Object getAuthenticationDetails(String key) {
        Map<String, Object> authenticationDetails = getAuthenticationDetails();
        return authenticationDetails.get(key);
    }

    /**
     * 获取当前令牌内容
     *
     * @return String 令牌内容
     */
    public static String getCurrentTokenValue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
    }

    /**
     * 获取用户角色信息
     *
     * @return 角色集合
     */
    public static Set<String> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
            .filter(granted -> StringUtils.startsWith(granted.getAuthority(), "ROLE_"))
            .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    /**
     * @return
     */
    public static String getClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
            return auth2Authentication.getOAuth2Request().getClientId();
        }

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (servletRequestAttributes.getRequest() != null) {
                BasicAuthenticationConverter basicAuthenticationConverter = new BasicAuthenticationConverter();
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = basicAuthenticationConverter.convert(servletRequestAttributes.getRequest());
                if (usernamePasswordAuthenticationToken != null) {
                    return usernamePasswordAuthenticationToken.getName();
                }
            }
        }

        //内部接口没有传递 token 时，header 中传递了客户端ID
//		if (WebUtils.getRequest().isPresent()) {
//			return WebUtils.getClientId();
//		}
        return null;
    }
}
