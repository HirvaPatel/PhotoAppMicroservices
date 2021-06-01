package com.example.demo.api.users.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

import com.example.demo.api.users.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{
	
	Environment environment;
	UserService userService;
	BCryptPasswordEncoder bcryptPasswordEncoder;
	
	@Autowired
	public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bcryptEncoder) {
		this.environment = env;
		this.userService = userService;
		this.bcryptPasswordEncoder = bcryptEncoder;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/**").permitAll()
		.and()
		.addFilter(getAuthenticationFilter());		
		http.headers().frameOptions().disable();
	}
	
	AuthenticationFilter getAuthenticationFilter() throws Exception{
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment, authenticationManager());
//		authenticationFilter.setAuthenticationManager(authenticationManager());
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return authenticationFilter;
	}
	
	 @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(bcryptPasswordEncoder);
	}
}
