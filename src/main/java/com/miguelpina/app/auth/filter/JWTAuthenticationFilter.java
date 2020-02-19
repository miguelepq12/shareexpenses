package com.miguelpina.app.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.miguelpina.app.models.service.IUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miguelpina.app.auth.service.JWTService;
import com.miguelpina.app.auth.service.JWTServiceImpl;
import org.springframework.web.cors.CorsUtils;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManger;
	private JWTService jwtService;
	private IUserService userService;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManger,JWTService jwtService,IUserService userService) {
		this.authenticationManger = authenticationManger;
		this.jwtService=jwtService;
		this.userService=userService;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login","POST"));
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		String username = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		String password = obtainPassword(request);

		if(username!=null && password!=null) {
			logger.info("Username desde request parameter (form-data): "+username);
			logger.info("Password desde request parameter (form-data): "+password);
		}else {
			com.miguelpina.app.models.entity.User user=null;
			try {
				user=new ObjectMapper().readValue(request.getInputStream(), com.miguelpina.app.models.entity.User.class);
				username=user.getUsername();
				password=user.getPass();
				
				logger.info("Username desde request parameter (raw): "+username);
				logger.info("Password desde request parameter (raw): "+password);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		username = username.trim();

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				username, password);

		return authenticationManger.authenticate(authToken);
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String token=jwtService.create(authResult);
		
		response.addHeader(JWTServiceImpl.HEADER_STRING, JWTServiceImpl.TOKEN_PREFIX+token);
		
		Map<String, Object> body =new HashMap<String,Object>();
		body.put("token", token);
		body.put("user",userService.findByUsername(authResult.getName()));
		body.put("mensaje", String.format("%s, has iniciado sesión",authResult.getName()));
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(200);
		response.setContentType("application/json");
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
	
		Map<String, Object> body =new HashMap<String,Object>();
		body.put("mensaje", "Error de autenticación: Username o Password incorrecto!");
		body.put("error", failed.getMessage());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}
	
}
