package com.zversal.auth.controller;

import com.zversal.auth.repository.RTokenRepository;
import com.zversal.auth.repository.UsersRepository;
import com.zversal.auth.model.JwtUser;
import com.zversal.auth.model.RefreshToken;
import com.zversal.auth.security.JwtGenerator;
import com.zversal.auth.security.JwtValidator;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

	@Autowired
	private UsersRepository repo;
	
	@Autowired
	private RTokenRepository rToken;
	
	@Autowired
	private JwtGenerator jwtGenerator;
	private JwtUser jwtUser = new JwtUser();
	@Autowired
	private JwtValidator jwtValidator ;
	private RefreshToken refreshTokenO = new RefreshToken();
	
	

	public AuthenticationController(JwtGenerator jwtGenerator) {
		this.jwtGenerator = jwtGenerator;
	}
	@CrossOrigin(origins = "*", allowedHeaders="*")
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public Map<String,String> generate(@RequestBody JwtUser jwt) throws JSONException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		//JSONObject obj = new JSONObject();
		//RefreshToken refreshToken = new RefreshToken();
		//System.out.println(refreshToken.getRefreshToken());
		//refreshToken.setRefreshToken("Bhupinder");
		//System.out.println(refreshToken);
		HashMap<String, String> obj = new HashMap<>();
		
		
	//	rToken.save(refreshToken);
		String user = jwt.getUserName();
		jwtUser = repo.findByUserName(user);
		if (jwtUser != null) {
			String token = jwtGenerator.generate(jwtUser);
			String refreshToken = jwtGenerator.refreshToken(jwtUser);
			refreshTokenO.setRefreshToken(refreshToken);
			rToken.save(refreshTokenO);
			obj.put("token", token);
			obj.put("refreshToken",refreshToken);
			return obj;
		} else {
			obj.put("Failure","invalid User - No token" );
			return obj;
		}
	}
	@CrossOrigin(origins = "*", allowedHeaders="*")
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@RequestBody JwtUser user, HttpServletResponse response) {
		if (repo.findByUserName(user.getUserName()) != null) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return "UserName is Already Exist";
		} else {
			repo.save(user);
			return "Successfully Registered";
		}
	}
	
	@CrossOrigin(origins = "*", allowedHeaders="*")
	@RequestMapping(value = "/zversal/auth", method = RequestMethod.GET)
	public String hello() {
		return "Zversal";
	}
	
	@CrossOrigin(origins = "*", allowedHeaders="*")
	@RequestMapping(value="/validate", method= RequestMethod.GET)
	public String validateToken(HttpServletRequest request) {
		String header = request.getHeader("Authorisation");
		if (header == null || !header.startsWith("Token ")) {
			throw new RuntimeException("JWT Token is missing");
		}
		String authenticationToken = header.substring(6);
		JwtUser jwtUser2 = jwtValidator.validate(authenticationToken);
		if (jwtUser2 == null) {
			System.out.println("JWT Token is incorrect ---- validateToken()");
			return "Not valid";
			//throw new RuntimeException("JWT Token is incorrect");
		}
		else {
			return "Valid";
		}
	}

	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public String refreshToken(HttpServletRequest request) {
		String header = request.getHeader("Authorisation");
		System.out.println("HEADERRRRRRRR:::::::"+header);
		RefreshToken refreshToken = rToken.findByRefreshToken(header);
		//String refreshToken = rToken.find(header);
		if (refreshToken != null) {
			JwtUser jwtUser2 = new JwtUser();
			jwtUser2 = jwtValidator.validate(header);
			try {
			System.out.println("JWTUSERGET"+jwtUser2.getUserName());
			}
			catch (NullPointerException ne){
				return "Refresh Token expired";
			}
			System.out.println("JWTUSEREMAIL"+jwtUser2.getEmail());
			String token = jwtGenerator.generate(jwtUser2);
			return token;
		} else {
			return "failure";
		}
		/*
		 * String user = jwt.getUserName(); jwtUser = repo.findByUserName(user); if
		 * (jwtUser != null) { String token = jwtGenerator.generate(jwtUser); return
		 * token; } else { return "invalid User - No token"; }
		 */
	
	}
}
