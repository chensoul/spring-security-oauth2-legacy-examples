package com.chensoul.security.oauth2.authorization.configuration;

import com.chensoul.security.oauth2.common.support.CustomAuthenticationKeyGenerator;
import com.chensoul.security.oauth2.common.support.JwtSetAccessTokenConverter;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Map;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableConfigurationProperties(ResourceServerProperties.class)
@AllArgsConstructor
public class TokenConfiguration {
	private AuthorizationServerProperties properties;

	/**
	 * jwk token 配置
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jwk", matchIfMissing = true)
	public class JwkTokenConfig {
		private final String JWK_KID = RandomStringUtils.randomAlphanumeric(6);

		@Bean
		public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
			return new JwtTokenStore(jwtAccessTokenConverter);
		}

		@Bean
		public JwtAccessTokenConverter accessTokenConverter() {
			Map<String, String> customHeaders = Collections.singletonMap("kid", JWK_KID);
			return new JwtSetAccessTokenConverter(customHeaders, keyPair());
		}

		public KeyPair keyPair() {
			ClassPathResource ksFile = new ClassPathResource(properties.getJwt().getKeyStore());
			KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, properties.getJwt().getKeyStorePassword().toCharArray());
			return ksFactory.getKeyPair(properties.getJwt().getKeyAlias(), properties.getJwt().getKeyPassword().toCharArray());
		}

		@Bean
		public JWKSet jwkSet() {
			RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair().getPublic());
			builder.keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).keyID(JWK_KID);
			return new JWKSet(builder.build());
		}

		@Order(-1)
		@Configuration
		@AutoConfigureAfter(JwkTokenConfig.class)
		public class JwkSetEndpointConfiguration extends AuthorizationServerSecurityConfiguration {
			/**
			 * @param http the {@link HttpSecurity} to modify for enabling the endpoint.
			 * @throws Exception
			 */
			@Override
			protected void configure(HttpSecurity http) throws Exception {
				http.authorizeRequests().antMatchers("/jwks").permitAll()
					.and().requestMatchers().antMatchers("/jwks");
				super.configure(http);
			}

			@RestController
			public class JwkSetEndpoint {
				@Autowired
				private JWKSet jwkSet;

				/**
				 * @return
				 */
				@GetMapping("/jwks")
				public Map<String, Object> keys() {
					return this.jwkSet.toJSONObject();
				}
			}
		}
	}

	/**
	 * jdbc token 配置
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jdbc")
	public class JdbcTokenConfig {
		@Autowired
		private Environment env;

		@Value("classpath:schema.sql")
		private Resource schemaScript;

		@Value("classpath:data.sql")
		private Resource dataScript;

		/**
		 * @param dataSource
		 * @return
		 */
		@Bean
		public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
			final DataSourceInitializer initializer = new DataSourceInitializer();
			initializer.setDataSource(dataSource);
			initializer.setDatabasePopulator(databasePopulator());
			return initializer;
		}

		/**
		 * @return
		 */
		private DatabasePopulator databasePopulator() {
			final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
			populator.addScript(schemaScript);
			populator.addScript(dataScript);
			return populator;
		}

		/**
		 * @return data source
		 */
		@Bean
		public DataSource dataSource() {
			final DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
			dataSource.setUrl(env.getProperty("jdbc.url"));
			dataSource.setUsername(env.getProperty("jdbc.username"));
			dataSource.setPassword(env.getProperty("jdbc.password"));
			return dataSource;
		}

		/**
		 * @return token store
		 */
		@Bean
		public TokenStore tokenStore() {
			JdbcTokenStore jdbcTokenStore = new JdbcTokenStore(dataSource());
			jdbcTokenStore.setAuthenticationKeyGenerator(new CustomAuthenticationKeyGenerator());
			return jdbcTokenStore;
		}

		/**
		 * @return approval store
		 */
		@Bean
		public ApprovalStore approvalStore() {
			return new JdbcApprovalStore(dataSource());
		}

		/**
		 * @return authorization code services
		 */
		@Bean
		public AuthorizationCodeServices authorizationCodeServices() {
			return new JdbcAuthorizationCodeServices(dataSource());
		}
	}

	/**
	 * redis token 配置
	 */
	@AllArgsConstructor
	@Configuration
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "redis")
	public class RedisTokenConfig {
		private RedisConnectionFactory redisConnectionFactory;

		/**
		 * @return token store
		 */
		@Bean
		public TokenStore tokenStore() {
			RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
			redisTokenStore.setPrefix("custom:");
			redisTokenStore.setAuthenticationKeyGenerator(new CustomAuthenticationKeyGenerator());
			return redisTokenStore;
		}
	}
}
