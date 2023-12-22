package com.chensoul.oauth2.common.exception;

import com.chensoul.oauth2.common.model.EnumAware;
import com.chensoul.oauth2.common.model.ResultCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 包装器业务异常类实现
 * <p>
 *
 * @author chensoul
 * @since 3.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

	private static final ResultCode DEFAULT = ResultCode.INTERNAL_ERROR;

	private int code;

	private Serializable data;

	public BusinessException(String message) {
		this(message, DEFAULT.getCode(), null, null);
	}

	public BusinessException(String message, Throwable cause) {
		this(message, DEFAULT.getCode(), cause, null);
	}

	public BusinessException(String message, int code) {
		this(message, code, null, null);
	}

	public BusinessException(String message, int code, Throwable cause, Serializable data) {
		super(message, cause);
		this.code = code;
		this.data = data;
	}

	public BusinessException(EnumAware enumAware) {
		this(enumAware.getName(), enumAware.getCode(), null, null);
	}

	public BusinessException(EnumAware enumAware, String message) {
		this(message, enumAware.getCode(), null, null);
	}

	public BusinessException(EnumAware enumAware, Throwable cause) {
		this(enumAware.getName(), enumAware.getCode(), cause, null);
	}

	public BusinessException(EnumAware enumAware, Serializable data) {
		this(enumAware.getName(), enumAware.getCode(), null, data);
	}

	@Override
	public String getMessage() {
		return StringUtils.isBlank(super.getMessage()) ? DEFAULT.getName() : super.getMessage();
	}

	public Serializable getData() {
		return data;
	}

	public int getCode() {
		return code;
	}

}
