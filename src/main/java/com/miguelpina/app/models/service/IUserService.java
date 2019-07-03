package com.miguelpina.app.models.service;

import com.miguelpina.app.models.entity.User;

public interface IUserService {
	void save(User user);
	User findByUsername(String username);
	boolean isEmailValid(User user);
	boolean isUsernameValid(User user);
	void updateImg(String img,User user);
}
