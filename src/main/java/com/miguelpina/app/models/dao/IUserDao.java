package com.miguelpina.app.models.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.miguelpina.app.models.entity.User;

public interface IUserDao extends CrudRepository<User, Long> {

	User findByUsername(String username);
	
	boolean existsByUsername(String username);
	
	boolean existsByEmail(String email);

	@Modifying
	@Query("update User u set u.profileImg = ?1 where u = ?2")
	void updateImg(String profileImg,User user);
}
