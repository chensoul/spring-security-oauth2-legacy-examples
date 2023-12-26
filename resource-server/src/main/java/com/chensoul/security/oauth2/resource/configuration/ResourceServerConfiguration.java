package com.chensoul.security.oauth2.resource.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Import(org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration.class)
@Configuration
public class ResourceServerConfiguration {

	@EnableResourceServer
	@AllArgsConstructor
	@Configuration
	public class ResourceServerConfigurationInner extends ResourceServerConfigurerAdapter {
		private final ResourceServerTokenServices tokenServices;
		private final AccessDeniedHandler accessDeniedHandler;
		private final AuthenticationEntryPoint authenticationEntryPoint;
		private final PermitUrlProperties properties;


		@Override
		public void configure(final HttpSecurity http) throws Exception {
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
			properties.getAuthenticatedUrls().forEach(url -> registry.antMatchers(url.getMethod(), url.getUrl()).authenticated());
			properties.getIgnoreUrls().forEach(url -> registry.antMatchers(url).permitAll());
			properties.getInnerUrls().forEach(url -> registry.antMatchers(url.getMethod(), url.getUrl()).permitAll());

			registry.anyRequest().authenticated();
		}

		@Override
		public void configure(final ResourceServerSecurityConfigurer resources) {
			DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
			UserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();
			accessTokenConverter.setUserTokenConverter(userTokenConverter);
			if (tokenServices instanceof RemoteTokenServices) {
				RemoteTokenServices remoteTokenServices = (RemoteTokenServices) tokenServices;
				remoteTokenServices.setAccessTokenConverter(accessTokenConverter);
				resources.tokenServices(remoteTokenServices);
			} else {
				resources.tokenServices(tokenServices);
			}

			resources
				//无状态化,每次访问都需认证
				.stateless(true)
				//自定义Token异常信息,用于token校验失败返回信息
				.authenticationEntryPoint(authenticationEntryPoint)
				//授权异常处理
				.accessDeniedHandler(accessDeniedHandler);
		}
	}

}
