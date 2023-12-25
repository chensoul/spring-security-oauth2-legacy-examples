package com.chensoul.security.oauth2.authorization.configuration;

import static com.chensoul.security.oauth2.common.constants.SecurityConstants.DEFAULT_FIND_STATEMENT;
import static com.chensoul.security.oauth2.common.constants.SecurityConstants.DEFAULT_SELECT_STATEMENT;
import com.chensoul.security.oauth2.common.support.CacheableJdbcClientDetailsService;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configuration.ClientDetailsServiceConfiguration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;

/**
 * Client details configuration
 *
 * @author <a href="mailto:chensoul.eth@gmail.com">Chensoul</a>
 * @since 1.0.0
 */
@Configuration
public class ClientDetailsConfiguration {
	@Configuration
	@AllArgsConstructor
	@AutoConfigureBefore(ClientDetailsServiceConfiguration.class)
	@ConditionalOnProperty(prefix = "authorization", name = "client-type", havingValue = "memory", matchIfMissing = true)
	public static class InMemoryClientDetailsConfig {
		private PasswordEncoder passwordEncoder;

		@Bean
		public ClientDetailsService clientDetailsService() {
			InMemoryClientDetailsService clientDetailsService = new InMemoryClientDetailsService();
			Map<String, BaseClientDetails> baseClientDetailsMap = new HashMap<>();
			BaseClientDetails baseClientDetails = new BaseClientDetails("client", "", "server,profile", "authorization_code,password,refresh_token,client_credentials", "http://localhost:8010/");
			baseClientDetails.setClientSecret(passwordEncoder.encode("secret"));
			baseClientDetailsMap.put("client", baseClientDetails);
			clientDetailsService.setClientDetailsStore(baseClientDetailsMap);
			return clientDetailsService;
		}

		@Bean
		public AuthorizationCodeServices authorizationCodeServices() {
			return new InMemoryAuthorizationCodeServices();
		}
	}

	@Configuration
	@AllArgsConstructor
	@AutoConfigureBefore(ClientDetailsServiceConfiguration.class)
	@ConditionalOnProperty(prefix = "authorization", name = "client-type", havingValue = "jdbc")
	public static class JdbcClientDetailsConfig {
		private final DataSource dataSource;
		@Nullable
		private final RedisTemplate<String, Object> redisTemplate;

		@Bean
		public ClientDetailsService clientDetailsService() {
			if (redisTemplate == null) {
				JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
				clientDetailsService.setSelectClientDetailsSql(DEFAULT_SELECT_STATEMENT);
				clientDetailsService.setFindClientDetailsSql(DEFAULT_FIND_STATEMENT);
				return clientDetailsService;
			}
			CacheableJdbcClientDetailsService clientDetailsService = new CacheableJdbcClientDetailsService(dataSource, redisTemplate);
			clientDetailsService.setSelectClientDetailsSql(DEFAULT_SELECT_STATEMENT);
			clientDetailsService.setFindClientDetailsSql(DEFAULT_FIND_STATEMENT);
			return clientDetailsService;
		}

		@Bean
		public AuthorizationCodeServices authorizationCodeServices() {
			return new JdbcAuthorizationCodeServices(dataSource);
		}
	}
}
