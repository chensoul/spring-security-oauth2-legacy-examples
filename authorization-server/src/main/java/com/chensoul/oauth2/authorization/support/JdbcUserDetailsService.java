package com.chensoul.oauth2.authorization.support;

import com.chensoul.oauth2.common.model.LoggedUser;
import com.chensoul.oauth2.common.model.LoggedUserDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * jdbc user details service
 */
public class JdbcUserDetailsService implements UserDetailsService {
	/**
	 * jdbc template
	 */
	private JdbcTemplate jdbcTemplate;

	/**
	 * @param dataSource data source
	 */
	public JdbcUserDetailsService(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * @param username the username identifying the user whose data is required. Cannot be null.
	 * @return
	 * @throws UsernameNotFoundException
	 */
	@Override
	public LoggedUserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		String userSQLQuery = "SELECT * FROM USERS WHERE USERNAME=? limit 1";
		LoggedUser loggedUser = jdbcTemplate.queryForObject(userSQLQuery, new String[]{username}, new RowMapper<LoggedUser>() {
			@Nullable
			@Override
			public LoggedUser mapRow(ResultSet rs, int rowNum) throws SQLException {
				LoggedUser user = new LoggedUser();
				user.setId(rs.getLong("ID"));
				user.setName(rs.getString("name"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setPermissions(Arrays.asList("ADMIN"));
				return user;
			}
		});
		return new LoggedUserDetails(loggedUser);
	}
}
