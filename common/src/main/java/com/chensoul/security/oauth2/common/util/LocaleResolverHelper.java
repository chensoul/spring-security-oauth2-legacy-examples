package com.chensoul.security.oauth2.common.util;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class LocaleResolverHelper {
	public static final String LOCALE_PARAM_NAME = "lang";

	private static LocaleResolver localeResolver;

	public static void setLocaleResolver(LocaleResolver localeResolver) {
		LocaleResolverHelper.localeResolver = localeResolver;
	}

	public static Locale resolveLocale() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		HttpServletResponse response = attributes.getResponse();
		return resolveLocale(request, response);
	}

	public static Locale resolveLocale(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = null;
		String newLocale = request.getParameter(LOCALE_PARAM_NAME);
		if (newLocale != null) {
			locale = StringUtils.parseLocale(newLocale);
		}
		if (hasNoneLocale(locale)) {
			locale = request.getLocale();
		}

		LocaleResolver localeResolver = getLocaleResolver(request);
		if (hasNoneLocale(locale)) {
			locale = localeResolver.resolveLocale(request);
		}
		if (hasNoneLocale(locale)) {
			locale = LocaleContextHolder.getLocale();
		}
		if (!hasNoneLocale(locale)) {
			localeResolver.setLocale(request, response, locale);
			request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, localeResolver);

			LocaleContextHolder.setLocale(locale);
		}
		return locale;
	}

	private static LocaleResolver getLocaleResolver(HttpServletRequest request) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver == null) {
			localeResolver = LocaleResolverHelper.localeResolver;
		}

		if (localeResolver == null) {
			localeResolver = new CookieLocaleResolver();
		}

		return localeResolver;
	}

	private static boolean hasNoneLocale(Locale locale) {
		return locale == null || StringUtils.isEmpty(locale.toString());
	}
}
