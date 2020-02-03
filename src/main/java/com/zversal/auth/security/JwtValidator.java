package com.zversal.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.zversal.auth.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtValidator {

	@Autowired
	private Environment env;

	public JwtUser validate(String token) {
		System.out.println("Validator");
		JwtUser jwtUser = null;
		try {
			Claims body = Jwts.parser().setSigningKey( env
					.getProperty("security.authentication.jwt.secret-key")).parseClaimsJws(token).getBody();
			jwtUser = new JwtUser();
			jwtUser.setUserName(body.getSubject());
			jwtUser.setEmail((String) body.get("email"));

		} catch (ExpiredJwtException ex) {
			System.out.println("Jwt Token is Expired");
		} catch (Exception e) {
			System.out.println("Jwt Token is incorrect");
		}
		return jwtUser;
	}
	/*
	 * public RefreshTokenValidate(String token) { System.out.prin }
	 */
}
