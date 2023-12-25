
package com.chensoul.security.oauth2.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 只定义常见的、需要显示给用户的信息描述信息
 * <p>
 *
 * @author chensoul
 * @since 3.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements EnumAware {
    SUCCESS(0, "SUCCESS"),

    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),

    INTERNAL_ERROR(500, "INTERNAL_ERROR"),
    INNER_SERVICE_ERROR(501, "INNER_SERVICE_ERROR"),
    INNER_SERVICE_UNAVAILABLE(502, "INNER_SERVICE_UNAVAILABLE"),
    REQUEST_TIMEOUT(503, "REQUEST_TIMEOUT"),
    ;

    private int code;
    private String name;

}
