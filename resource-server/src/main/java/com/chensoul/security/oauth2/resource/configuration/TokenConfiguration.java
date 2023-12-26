package com.chensoul.security.oauth2.resource.configuration;

import com.chensoul.security.oauth2.common.support.CustomAuthenticationKeyGenerator;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @see OAuth2ResourceServerConfiguration
 * @see ResourceServerTokenServicesConfiguration
 *
 */
@Configuration
@ConditionalOnProperty(prefix = "security.oauth2.resource.jwk", name = "key-set-uri", havingValue = "", matchIfMissing = true)
@Import({TokenConfiguration.JwkTokenConfiguration.class,
	TokenConfiguration.JdbcTokenConfiguration.class,
	TokenConfiguration.RedisTokenConfiguration.class
})
public class TokenConfiguration {

	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jwk", matchIfMissing = true)
	public class JwkTokenConfiguration {
	}

	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "redis")
	public class RedisTokenConfiguration {
		private RedisConnectionFactory redisConnectionFactory;

		@Bean
		public TokenStore tokenStore() {
			RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
			redisTokenStore.setPrefix("custom:");
			redisTokenStore.setAuthenticationKeyGenerator(new CustomAuthenticationKeyGenerator());
			return redisTokenStore;
		}
	}

	@AllArgsConstructor
	@ConditionalOnProperty(prefix = "security.oauth2", name = "token-type", havingValue = "jdbc")
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
	}
}
