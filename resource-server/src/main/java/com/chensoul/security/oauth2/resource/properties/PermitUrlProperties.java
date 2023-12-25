package com.chensoul.security.oauth2.resource.properties;

import com.chensoul.security.oauth2.common.annotation.Inner;
import com.chensoul.security.oauth2.common.util.SpringContextHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 资源服务器对外直接暴露URL,如果设置contex-path 要特殊处理
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "security.oauth2.client")
public class PermitUrlProperties implements InitializingBean {
	private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");
	private static final AntPathMatcher MATCHER = new AntPathMatcher();

	/**
	 * 免认证资源路径，支持通配符
	 */
	private List<String> ignoreUrls = new ArrayList<>();
	/**
	 * 免认证资源路径，支持通配符
	 */
	private List<Url> innerUrls = new ArrayList<>();
	/**
	 * 必须认证资源路径，支持通配符
	 */
	private List<Url> authenticatedUrls = new ArrayList<>();

	@Override
	public void afterPropertiesSet() {
		RequestMappingHandlerMapping mapping = SpringContextHolder.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

		List<Url> otherUrls = new ArrayList<>();
		for (RequestMappingInfo info : map.keySet()) {
			HandlerMethod handlerMethod = map.get(info);
			// 获取方法上边的注解 替代 path variable 为 *
			Inner method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Inner.class);
			if (method == null) {
				continue;
			}
			addInnerUrls(info, method);

			// 获取类上边的注解, 替代 path variable 为 *
			Inner controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Inner.class);
			addInnerUrls(info, controller);

			if (Objects.isNull(method) && Objects.isNull(controller)) {
				info.getPatternsCondition()
					.getPatterns()
					.forEach(url -> info.getMethodsCondition()
						.getMethods()
						.forEach(requestMethod -> otherUrls
							.add(new Url().setMethod(HttpMethod.resolve(requestMethod.name()))
								.setUrl(RegExUtils.replaceAll(url, PATTERN, "*")))));
			}
		}
		if (!CollectionUtils.isEmpty(otherUrls) && !CollectionUtils.isEmpty(innerUrls)) {
			// 在otherUrls中如果有被innerUrl命中的，需要加入到authenticatedUrls中
			otherUrls.forEach(otherUrl -> innerUrls.forEach(innerUrl -> {
				if (innerUrl.method.equals(otherUrl.method) && MATCHER.match(innerUrl.url, otherUrl.url)) {
					authenticatedUrls.add(otherUrl);
				}
			}));
		}
		log.info("authenticatedUrls: {}", authenticatedUrls);
	}

	private void addInnerUrls(RequestMappingInfo info, Inner inner) {
		if (inner != null && info.getPatternsCondition() != null) {
			info.getPatternsCondition().getPatterns().forEach(pattern ->
				info.getMethodsCondition().getMethods().forEach(requestMethod -> {
					Url url = new Url().setMethod(HttpMethod.resolve(requestMethod.name())).setUrl(RegExUtils.replaceAll(pattern, PATTERN, "*"));
					innerUrls.add(url);
				}));
		}
	}

	@Data
	@Accessors(chain = true)
	public class Url {
		private HttpMethod method;
		private String url;
	}
}
