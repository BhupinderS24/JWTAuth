package com.zversal.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//import com.zversal.auth.model.JwtUser;
import com.zversal.auth.model.RefreshToken;

@Repository
public interface RTokenRepository extends MongoRepository<RefreshToken , Long> {
	RefreshToken findByRefreshToken (String refreshToken);

	void save(String string);
}
