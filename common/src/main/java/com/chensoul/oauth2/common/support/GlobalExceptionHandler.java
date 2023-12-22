package com.chensoul.oauth2.common.support;

import com.chensoul.oauth2.common.exception.BusinessException;
import com.chensoul.oauth2.common.model.RestResponse;
import com.chensoul.oauth2.common.model.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 捕获全局异常，返回http状态码为200，并封装异常信息
 */
@Slf4j
@RestControllerAdvice
@Order
public class GlobalExceptionHandler {
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public RestResponse handleMissingServletRequestParameterException(
		HttpServletRequest request, MissingServletRequestParameterException ex) {
		log.error("{}, 缺少参数", request.getRequestURI(), ex);

		return RestResponse.error(String.format("缺少参数: %s", ex.getParameterName()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public RestResponse handleMethodArgumentTypeMismatchException(
		HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
		log.error("{}, 参数类型不匹配", request.getRequestURI(), ex);

		Map<String, String> details = new HashMap();
		details.put("paramName", ex.getName());
		details.put("paramValue", Optional.ofNullable(ex.getValue()).map(Object::toString).orElse(null));
		details.put("errorMessage", ex.getMessage());

		return RestResponse.of(ResultCode.INTERNAL_ERROR.getCode(), String.format("参数类型不匹配: %s", ex.getName()), details);
	}

	@ExceptionHandler(value = {BindException.class})
	@ResponseStatus(HttpStatus.OK)
	public RestResponse handleBindException(HttpServletRequest request, BindException e) {
		log.error("{}, 参数不合法", request.getRequestURI());

		ArrayList details = new ArrayList<Map<String, String>>();
		e.getBindingResult()
			.getFieldErrors()
			.forEach(
				fieldError -> {
					Map<String, String> detail = new HashMap<>();
					detail.put("objectName", fieldError.getObjectName());
					detail.put("field", fieldError.getField());
					detail.put("rejectedValue", "" + fieldError.getRejectedValue());
					detail.put("errorMessage", fieldError.getDefaultMessage());
					details.add(detail);
				});

		return RestResponse.of(ResultCode.INTERNAL_ERROR.getCode(), "参数不合法", details);
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler({BusinessException.class})
	public RestResponse handleBusinessException(HttpServletRequest request, final BusinessException e) {
		log.error("{}, 业务异常", request.getRequestURI(), e);

		return RestResponse.of(e.getCode(), e.getMessage(), e.getData());
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler({Exception.class})
	public RestResponse handleException(HttpServletRequest request, final Exception e) {
		log.error("{}, 系统异常", request.getRequestURI(), e);
		return RestResponseMessageResolver.resolver(e);
	}

//	@ResponseStatus(HttpStatus.FORBIDDEN)
//	@ExceptionHandler({AccessDeniedException.class})
//	public RestResponse handleAccessDeniedException(HttpServletRequest request, final AccessDeniedException e) {
//		log.error("{}, 无权限访问", request.getRequestURI(), e);
//		return RestResponse.error(ResultCode.FORBIDDEN);
//	}


}
