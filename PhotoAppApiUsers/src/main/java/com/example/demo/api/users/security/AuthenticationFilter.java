package com.example.demo.api.users.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.api.users.service.UserService;
import com.example.demo.api.users.shared.UserDto;
import com.example.demo.api.users.ui.model.LoginUserRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private UserService userService;
	private Environment env;
	
	@Autowired
	public AuthenticationFilter(UserService userService, Environment env, AuthenticationManager authManager) {
		this.userService = userService;
		this.env = env;
		super.setAuthenticationManager(authManager);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
			try {
				LoginUserRequestModel cred = new ObjectMapper().readValue(request.getInputStream(), LoginUserRequestModel.class);
				
				return getAuthenticationManager().authenticate(
						new UsernamePasswordAuthenticationToken(
								cred.getEmail(), 
								cred.getPassword(),
								new ArrayList<>())
						
						);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String username = ((User) authResult.getPrincipal()).getUsername();
		UserDto userDetails = userService.getUserDetailsByEmail(username);
		String token = Jwts.builder()
						.setSubject(userDetails.getUserId())
						.setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time"))))
						.signWith(SignatureAlgorithm.HS256, env.getProperty("token.secret"))
						.compact();
		response.addHeader("token", token);
		response.addHeader("userId", userDetails.getUserId());
	}
}
