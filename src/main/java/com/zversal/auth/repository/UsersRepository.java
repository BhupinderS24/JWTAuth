package com.zversal.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.zversal.auth.model.JwtUser;

@Repository
public interface UsersRepository extends MongoRepository<JwtUser, Long> {
	JwtUser findByUserName(String username);
}