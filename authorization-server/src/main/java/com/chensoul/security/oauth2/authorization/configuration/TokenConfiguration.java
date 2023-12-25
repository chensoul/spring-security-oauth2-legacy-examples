package com.chensoul.security.oauth2.authorization.configuration;

import com.chensoul.security.oauth2.common.support.CustomAuthenticationKeyGenerator;
import com.chensoul.security.oauth2.common.support.CustomJwtTokenEnhancer;
import com.chensoul.security.oauth2.common.util.RSAUtil;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Map;

@Configuration
public class TokenConfiguration {
	/**
	 * jwt token 配置
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "authorization", name = "token-type", havingValue = "jwt", matchIfMissing = true)
	public class JwtTokenConfig {
		String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDbU07CCkJxHjnj\n" +
			"ZSzpyGXI7kdbEZcdDfNq4xKkaA9gMg/yiQ7pdYe1c1er2RpdvH2qunDrTn/vfFtt\n" +
			"ItgardcWg9VopE5WooBkwamHhI5WXuVEhI5z+Qcs8pxoxoeYY/NZ4eGa5K4znJTh\n" +
			"3g03X+BV5WCJ88fVxBlJ29VJO2Iizs1zt3R4aRHZr5UQi4fEU1jm21Y+As9kLi+H\n" +
			"3RaEUBznIRL2KWr72mWD8Q4GQyvv7NIAh21kXY+9TyLZ1i4NTMUPW8PaK1uMSd3e\n" +
			"RkMko8CdZbRCR6umx1KaXbN3FuwwRp0M58hGoqhAMeMGlkre8YE7rkwuTx2DpnW6\n" +
			"RLroPdOVAgMBAAECggEABXcI8lrRFwJ6zMi7msOsjZoqWwGQM2AinZo037XcFDiL\n" +
			"Hy79GSIdlI7gM18Agi9tLjDhGWvmUjBWEt0YZ2nHfz8ftTDK5uu7qUcOE1thNJO+\n" +
			"wLtQ1WDyJdORxCaWQFzVNnsjZPmnwY94QjyHNccEzv8dLbLOWuw8+RMqOF3rbxSO\n" +
			"Rw7Aby0KvZt3aLnfpM7t3yR0Uf7OTXnYV4v3V/D61CzonFfnq/6cYXam8afGucED\n" +
			"hTSNBsj33SyFkLDkbGn+yQZZQcKidKsRqh//gYaXngZJH1Kb0yaX9zRAzKGbBY7R\n" +
			"s0iD7SKyeoRVwNl3+QbDMq4JTFaSSHZWXId6xcJiQQKBgQD1kdo66vHyQMRdJ+4m\n" +
			"v+sXZivU12N6r4CxCQowya2kK3py6PuiA3RiYzhVCZTCttunm2gRA75noGcCGaWW\n" +
			"a/ZBe3Fg9pF2HONZc5gxlxDPs/4dtARCPMoaMGUF0KPvJnVFdoACLnmrmpFNo2T9\n" +
			"yh+1GiHaDlTrXITdtbCY+G3jkQKBgQDkpBfvac38ORoWzPrBAh9o1wNNmOy6FvKA\n" +
			"AGvrOvnToiOaDgniEZf2QuQ9aBw+p/EkZ05awdFghtIQkgdYP5tNpZv1cucl+b5B\n" +
			"IiZd0PfgWsMd6CH/UhhSoC2qOC3AgDOYKMqpJbN47e0LFyA9zB8MVgdatT1Z92B8\n" +
			"ImOk9M3lxQKBgQCe1FslyFf8q89VwUHTZkYTuE2ZOEZrT6AY4RPqQkdA664jLHW0\n" +
			"cC+Sg0IoHi5NUh/35BgHCTjZpXF6dkQXanS4HbNTV6b1kfGqJY3GZULGVFCDva8T\n" +
			"eGXvUmXEwRGG2IT5DI/22UBwsWRRpuDnFRg09lX2x1Y9dHWrQJycE/JfQQKBgBw/\n" +
			"jdl9h/6Oi4Ofpads/LZ2Qik3KKBBL4rTAoJYkf8svBtPerccl1rMnuhbUYoIM8nc\n" +
			"nZim85hqb8+uSwwDJ8vrFx63KInPlkrzziuvIsDsVcj/IhwqZ2jl815hfH2ZGUbG\n" +
			"W2uUhdz+AFhrbgdUjc13X4c8dy0DXD9Z/FeoCzxFAoGBAM/RVoHMECxGO3mJWes9\n" +
			"bNR9RijFRNN9n6EjYfZd5CniqoisbOdsPZ1I+W7g8R4eOpjTak6y07C0SfwYACv9\n" +
			"S43/eOG/EREeMz8KEM9u5uWl89r0UVL5t1CT5snco6TXMfV2pSYFbNrQ6wpbMqLE\n" +
			"MJsrviLIfcG2bTXY/ReZC61z";
		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA21NOwgpCcR4542Us6chl\n" +
			"yO5HWxGXHQ3zauMSpGgPYDIP8okO6XWHtXNXq9kaXbx9qrpw605/73xbbSLYGq3X\n" +
			"FoPVaKROVqKAZMGph4SOVl7lRISOc/kHLPKcaMaHmGPzWeHhmuSuM5yU4d4NN1/g\n" +
			"VeVgifPH1cQZSdvVSTtiIs7Nc7d0eGkR2a+VEIuHxFNY5ttWPgLPZC4vh90WhFAc\n" +
			"5yES9ilq+9plg/EOBkMr7+zSAIdtZF2PvU8i2dYuDUzFD1vD2itbjEnd3kZDJKPA\n" +
			"nWW0QkerpsdSml2zdxbsMEadDOfIRqKoQDHjBpZK3vGBO65MLk8dg6Z1ukS66D3T\n" +
			"lQIDAQAB";

		/**
		 * @return jwt token converter
		 */
		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter() {
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			//对称加密
//		converter.setSigningKey("");

			//非对称加密
			converter.setSigner(new RsaSigner((RSAPrivateKey) RSAUtil.getPrivateKeyFromString(privateKey)));
			// 可省略公钥
			converter.setVerifier(new RsaVerifier((RSAPublicKey) RSAUtil.getPublicKeyFromString(publicKey)));
			return converter;
		}

//	@Bean
//	public KeyPair keyPair() {
//		Resource keyStore = new ClassPathResource("mytest.jks");
//		char[] keyStorePassword = "mypass".toCharArray();
//		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(keyStore, keyStorePassword);
//
//		String keyAlias = "mytest";
//		char[] keyPassword = "mypass".toCharArray();
//		return keyStoreKeyFactory.getKeyPair(keyAlias, keyPassword);
//	}
//
//	@Bean
//	public JwtAccessTokenConverter jwtAccessTokenConverter(KeyPair keyPair) {
//		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//		converter.setKeyPair(keyPair);
//		return converter;
//	}

		/**
		 * 配置jwt token存储
		 */
		@Bean
		public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
			return new JwtTokenStore(jwtAccessTokenConverter);
		}


		/**
		 * jwt token增强, 加入自定义信息
		 *
		 * @param jwtAccessTokenConverter jwt token converter
		 * @return
		 */
		@Bean
		public TokenEnhancer tokenEnhancer(JwtAccessTokenConverter jwtAccessTokenConverter) {
			TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
			//自定义 Token 增强器, 集合元素位置不同，生成自定义参数位置不同
			enhancerChain.setTokenEnhancers(Arrays.asList(new CustomJwtTokenEnhancer(), jwtAccessTokenConverter));
			return enhancerChain;
		}

		/**
		 * @return default token services
		 */
		@Bean
		@Primary
		public DefaultTokenServices defaultTokenServices() {
			DefaultTokenServices services = new DefaultTokenServices();
			services.setTokenStore(tokenStore(jwtAccessTokenConverter()));
			return services;
		}

		/**
		 * 加入jwk-set-uri，提供给资源服务器
		 */
		@Order(-1)
		@Configuration
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

			/**
			 *
			 */
			@FrameworkEndpoint
			@ConditionalOnBean(KeyPair.class)
			class JwkSetEndpointByKeyPair {
				/**
				 * @param keyPair
				 * @return
				 */
				@ResponseBody
				@GetMapping("/jwks")
				public Map<String, Object> getKey(KeyPair keyPair) {
					RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
					RSAKey key = new RSAKey.Builder(publicKey).build();
					return new JWKSet(key).toJSONObject();
				}
			}

			@FrameworkEndpoint
			@ConditionalOnMissingBean(KeyPair.class)
			class JwkSetEndpointByPublicKey {
				/**
				 * @return
				 */
				@ResponseBody
				@GetMapping("/jwks")
				public Map<String, Object> getKey() {
					RSAPublicKey rsaPublicKey = (RSAPublicKey) RSAUtil.getPublicKeyFromString(publicKey);
					RSAKey key = new RSAKey.Builder(rsaPublicKey).build();
					return new JWKSet(key).toJSONObject();
				}
			}
		}
	}

	/**
	 * jdbc token 配置
	 */
	@Configuration
	@ConditionalOnProperty(prefix = "authorization", name = "token-type", havingValue = "jdbc")
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
	@ConditionalOnProperty(prefix = "authorization", name = "token-type", havingValue = "redis")
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
