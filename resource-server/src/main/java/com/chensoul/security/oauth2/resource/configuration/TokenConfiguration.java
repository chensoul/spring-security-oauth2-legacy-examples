package com.chensoul.security.oauth2.resource.configuration;

import com.chensoul.security.oauth2.common.support.CustomAccessTokenConverter;
import com.chensoul.security.oauth2.common.support.CustomAuthenticationKeyGenerator;
import com.chensoul.security.oauth2.common.support.CustomJwtClaimsSetVerifier;
import com.chensoul.security.oauth2.common.util.RSAUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.*;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

@Configuration
@ConditionalOnProperty(prefix = "spring.security.oauth2.resourceserver.jwt", name = "jwk-set-uri", havingValue = "", matchIfMissing = true)
@Import({TokenConfiguration.JwtTokenConfiguration.class,
	TokenConfiguration.JdbcTokenConfiguration.class,
	TokenConfiguration.RedisTokenConfiguration.class,
	TokenConfiguration.RemoteTokenConfiguration.class}
)
public class TokenConfiguration {
	@ConditionalOnProperty(prefix = "authorization", name = "token-type", havingValue = "jwt", matchIfMissing = true)
	public class JwtTokenConfiguration {
		/**
		 * jwt TokenStore 实现
		 *
		 * @return
		 */
		@Bean
		public TokenStore tokenStore() {
			return new JwtTokenStore(jwtAccessTokenConverter());
		}

		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter() {
			final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			converter.setAccessTokenConverter(new CustomAccessTokenConverter());
			converter.setJwtClaimsSetVerifier(jwtClaimsSetVerifier());

			// 对称加密
			//		converter.setSigningKey("123456");

			// 非对称加密
			String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA21NOwgpCcR4542Us6chl\n" +
				"yO5HWxGXHQ3zauMSpGgPYDIP8okO6XWHtXNXq9kaXbx9qrpw605/73xbbSLYGq3X\n" +
				"FoPVaKROVqKAZMGph4SOVl7lRISOc/kHLPKcaMaHmGPzWeHhmuSuM5yU4d4NN1/g\n" +
				"VeVgifPH1cQZSdvVSTtiIs7Nc7d0eGkR2a+VEIuHxFNY5ttWPgLPZC4vh90WhFAc\n" +
				"5yES9ilq+9plg/EOBkMr7+zSAIdtZF2PvU8i2dYuDUzFD1vD2itbjEnd3kZDJKPA\n" +
				"nWW0QkerpsdSml2zdxbsMEadDOfIRqKoQDHjBpZK3vGBO65MLk8dg6Z1ukS66D3T\n" +
				"lQIDAQAB";
			converter.setVerifier(new RsaVerifier((RSAPublicKey) RSAUtil.getPublicKeyFromString(publicKey)));

			return converter;
		}

		public JwtClaimsSetVerifier jwtClaimsSetVerifier() {
			return new DelegatingJwtClaimsSetVerifier(Arrays.asList(issuerClaimVerifier(), new CustomJwtClaimsSetVerifier()));
		}

		public JwtClaimsSetVerifier issuerClaimVerifier() {
			try {
				return new IssuerClaimVerifier(new URL("https://auth.chensoul.com"));
			} catch (final MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		@Bean
		public ResourceServerTokenServices resourceServerTokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setTokenStore(tokenStore());
			return tokenServices;
		}
	}

	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "authorization", name = "token-type", havingValue = "redis")
	public class RedisTokenConfiguration {
		private RedisConnectionFactory redisConnectionFactory;

		@Bean
		public TokenStore tokenStore() {
			RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
			redisTokenStore.setPrefix("custom:");
			redisTokenStore.setAuthenticationKeyGenerator(new CustomAuthenticationKeyGenerator());
			return redisTokenStore;
		}

		@Bean
		public ResourceServerTokenServices resourceServerTokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setTokenStore(tokenStore());
			return tokenServices;
		}
	}

	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "authorization", name = "token-type", havingValue = "jdbc")
	public class JdbcTokenConfiguration {
		private Environment env;

		@Bean
		public DataSource dataSource() {
			final DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
			dataSource.setUrl(env.getProperty("jdbc.url"));
			dataSource.setUsername(env.getProperty("jdbc.username"));
			dataSource.setPassword(env.getProperty("jdbc.password"));
			return dataSource;
		}

		@Bean
		public TokenStore tokenStore() {
			JdbcTokenStore jdbcTokenStore = new JdbcTokenStore(dataSource());
			return jdbcTokenStore;
		}

		@Bean
		public ResourceServerTokenServices resourceServerTokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setTokenStore(tokenStore());
			return tokenServices;
		}
	}

	/**
	 * 运程 Token 服务实现
	 * 1. 引入下面依赖
	 * <pre>
	 * <dependency>
	 *     <groupId>org.springframework.security.oauth.boot</groupId>
	 *     <artifactId>spring-security-oauth2-autoconfigure</artifactId>
	 * </dependency>
	 * </pre>
	 * <p>
	 * 2. yml 配置文件
	 *
	 * <pre>
	 * security:
	 *   oauth2:
	 *     client:
	 *       client-id: client
	 *       client-secret: secret
	 *     resource:
	 *       user-info-uri: http://localhost:8000/userinfo
	 *       token-info-uri: http://localhost:8000/oauth/check_token
	 * </pre>
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "authorization", name = "token-type", havingValue = "remote")
	public class RemoteTokenConfiguration {

		@Bean
		@Primary
		public RestTemplate lbRestTemplate() {
			RestTemplate restTemplate = new RestTemplate();

			// 如果 RestTemplate 处理了异常，过滤器（ CocktailAuthExceptionEntryPoint ）就不会处理异常
			restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
				@Override
				public void handleError(ClientHttpResponse response) throws IOException {
					if (response.getRawStatusCode() != HttpStatus.BAD_REQUEST.value() && response.getRawStatusCode() != HttpStatus.UNAUTHORIZED.value()) {
						super.handleError(response);
					}
				}
			});
			return restTemplate;
		}
	}
}
