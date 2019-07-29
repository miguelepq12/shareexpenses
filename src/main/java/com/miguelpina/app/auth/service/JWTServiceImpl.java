package com.miguelpina.app.auth.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miguelpina.app.auth.SimpleGrantedAuthorityMixin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTServiceImpl implements JWTService {

	public static final String SECRET="Pizza, pasticho y hamburguesas son mi comida favorita";

	//3600000 Es igual a una hora, por lo tanto el token duraría 4 hrs
	public static final long EXPIRATION_DATE=(3600000l*24l);
	public static final String TOKEN_PREFIX="Bearer ";
	public static final String HEADER_STRING="Authorization";
	
	@Override
	public String create(Authentication auth) throws  IOException  {
		User user=(User) auth.getPrincipal();
		
		Collection<? extends GrantedAuthority> roles=auth.getAuthorities();
		
		Claims claims= Jwts.claims();
		claims.put("authorities",new ObjectMapper().writeValueAsString(roles));
		
		String token= Jwts.builder()
				.setClaims(claims)
				.setSubject(user.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_DATE))
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
				.compact();
		
		return token;
	}

	@Override
	public boolean validate(String token) {
		try {
			getClaims(token);
			return true;
		}catch(JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public Claims getClaims(String token) {
		Claims claims=Jwts.parser()
				.setSigningKey(SECRET.getBytes())
				.parseClaimsJws(resolve(token))
				.getBody();
		return claims;
	}

	@Override
	public String getUsername(String token) {
		return getClaims(token).getSubject();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(String token) throws  IOException {
		Object roles=getClaims(token).get("authorities");
		

		Collection <? extends GrantedAuthority> authorities=Arrays.asList(new ObjectMapper()
				.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
				.readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));
		
		return authorities;
	}

	@Override
	public String resolve(String token) {
		return (token!=null && token.startsWith(TOKEN_PREFIX))?token.replaceAll(TOKEN_PREFIX, ""):null;
	}

}
