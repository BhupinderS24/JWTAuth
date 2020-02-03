package com.zversal.auth.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.zversal.auth.model.JwtUser;
import com.zversal.auth.model.RefreshToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtGenerator {
	@Autowired
	private Environment env;
   // private JwtUser jwtUser = new JwtUser();
    private JwtValidator validator = new JwtValidator();
	public String generate(JwtUser jwtUser) {
		Claims claims = Jwts.claims().setSubject(jwtUser.getUserName());
		claims.put("email", jwtUser.getEmail());
		String tokenExpirationInMilliSecondsString = env.getProperty("security.authentication.jwt.token-validity");
		long tokenExpirationInMilliSecondsLong = Long.parseLong(tokenExpirationInMilliSecondsString);
		//generateRefreshToken("bhupinder");
		return Jwts.builder().setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, env.getProperty("security.authentication.jwt.secret-key"))
				.setExpiration(new Date(System.currentTimeMillis() + tokenExpirationInMilliSecondsLong))
				.compact();
	}
	
	public String refreshToken(JwtUser jwtUser) {
		//JwtUser jwt = validator.validate(token);
		Claims claims = Jwts.claims().setSubject(jwtUser.getUserName());
		claims.put("email", jwtUser.getEmail());
		//Claims claims = Jwts.claims().setSubject(token);
		//claims.put("email", jwtUser.getEmail());
		//long tokenExpirationInMilliSecondsString = 600000;
		long tokenExpirationInMilliSecondsLong = 600000;
		//generateRefreshToken("bhupinder");
		return Jwts.builder().setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, env.getProperty("security.authentication.jwt.secret-key2"))
				.setExpiration(new Date(System.currentTimeMillis() + tokenExpirationInMilliSecondsLong))
				.compact();
	}
	
	
	
}
