package com.example.demo.api.users.data;

import org.springframework.data.repository.CrudRepository;

public interface UsersRespository extends CrudRepository<UserEntity, Long> {
	
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
}
