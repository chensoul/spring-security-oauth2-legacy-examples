package com.chensoul.security.oauth2.authorization.configuration;

import com.chensoul.security.oauth2.common.support.CustomWebResponseExceptionTranslator;
import java.security.Principal;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenKeyEndpoint;
import org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint;
import org.springframework.security.oauth2.provider.endpoint.WhitelabelErrorEndpoint;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Authorization Server Configuration
 *
 * <p>
 *
 * <a href="http://127.0.0.1:8000/oauth/authorize?client_id=client&redirect_uri=http://127.0.0.1:123&response_type=code&scope=server&state=beff3dfc-bad8-40db-b25f-e5459e3d6ad7">授权码模式：获取授权码</a> <br>
 * <a href="http://127.0.0.1:8000/oauth/token?code=oW9ca3&client_id=client&client_secret=secret&redirect_uri=http://127.0.0.1:123&grant_type=authorization_code">授权码模式：获取 Token</a> <br>
 * <a href="http://127.0.0.1:8000/oauth/authorize?client_id=client&redirect_uri=http://127.0.0.1:123&response_type=token&scope=server">implicit模式</a><br>
 * <a href="http://127.0.0.1:8000/oauth/token?client_id=client&client_secret=secret&grant_type=refresh_token&refresh_token=">刷新 Token</a> <br/>
 * <a href="http://127.0.0.1:8000/oauth/token?grant_type=client_credentials&client_id=client&client_secret=secret&scope=server">client_credentials模式</a> <br>
 * <a href="http://127.0.0.1:8000/oauth/check_token?token=">检查 token</a>
 * </p>
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">chensoul</a>
 * @see DefaultLoginPageGeneratingFilter 默认登录页面
 * @see OAuth2ClientAuthenticationProcessingFilter 一个 OAuth2 客户端过滤器，可用于从授权服务器获取 OAuth2 访问令牌，并将身份验证对象加载到 SecurityContext
 * @see ClientCredentialsTokenEndpointFilter 令牌端点的过滤器和身份验证端点
 * @see AuthorizationServerEndpointsConfiguration#authorizationEndpoint() 自定义页面
 * @see AuthorizationServerSecurityConfiguration Token 相关路径权限配置
 * @see WhitelabelApprovalEndpoint 用于显示授权服务器的批准页面的控制器。
 * @see WhitelabelErrorEndpoint 用于显示授权服务器错误页面的控制器。
 * @see AuthorizationEndpoint#authorize(Map, Map, SessionStatus, Principal) 授权码
 * @see AuthorizationEndpoint#approveOrDeny(Map, Map, SessionStatus, Principal) 授权码
 * @see TokenEndpoint#getAccessToken(Principal, Map) 获取 Token
 * @see TokenEndpoint#postAccessToken(Principal, Map) 获取 Token
 * @see TokenKeyEndpoint#getKey(Principal) 获取令牌签名的验证密钥
 * @see CheckTokenEndpoint#checkToken(String) 检查 Token
 * @see WhitelabelErrorEndpoint#handleError(HttpServletRequest) 异常处理
 * @see JwtAccessTokenConverter#enhance(OAuth2AccessToken, OAuth2Authentication) 增强Token
 * @see JwtTokenStore#readAccessToken(String)  读取 JWT Token
 * @since 4.0.0
 */
@Configuration
@EnableAuthorizationServer
@AllArgsConstructor
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final TokenStore tokenStore;
	@Nullable
	private final AccessTokenConverter accessTokenConverter;
	@Nullable
	private final TokenEnhancer tokenEnhancer;
	private final AuthenticationEntryPoint authenticationEntryPoint;
	private final AccessDeniedHandler accessDeniedHandler;
	private final ClientDetailsService clientDetailsService;
	private final AuthorizationCodeServices authorizationCodeServices;

	@Override
	public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
	}

	@Override
	public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
		security
			// 获取 Token：允许所有
			.tokenKeyAccess("permitAll()")
			// 检查 Token：允许所有
			.checkTokenAccess("permitAll()")
			// 允许 Client 进行表单验证（URL），否则将出现弹窗输入 ClientId、ClientSecret
			.allowFormAuthenticationForClients()
			.authenticationEntryPoint(authenticationEntryPoint)
			.accessDeniedHandler(accessDeniedHandler)
		;
	}

	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			// Token 存储方式
			.tokenStore(tokenStore)
			// 密码模式需要验证用户
			.authenticationManager(authenticationManager)
			// 授权码模式需要
			.authorizationCodeServices(authorizationCodeServices)
			// 刷新 Token 时查询用户
			.userDetailsService(userDetailsService)
			// 校验 scope
			.requestValidator(new DefaultOAuth2RequestValidator())
			// 自定义OAuth2异常转换
			.exceptionTranslator(new CustomWebResponseExceptionTranslator());

		// Token 转化器，JWT 需要
		if (accessTokenConverter != null) {
			endpoints.accessTokenConverter(accessTokenConverter);
		}

		// Token 增强，JWT 需要
		if (tokenEnhancer != null) {
			final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
			tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer));
			endpoints.tokenEnhancer(tokenEnhancerChain);
		}
	}

}
