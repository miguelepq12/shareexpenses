package com.miguelpina.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miguelpina.app.models.dao.IRoleDao;
import com.miguelpina.app.models.dao.IUserDao;
import com.miguelpina.app.models.entity.Role;
import com.miguelpina.app.models.entity.User;

@Service
public class UserServiceImp implements IUserService{

	@Autowired
	IUserDao userDao;
	@Autowired
	IRoleDao roleDao;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public void save(User user) {
		user.setPass(bCryptPasswordEncoder.encode(user.getPass()));
		List<Role> roles=new ArrayList<>();
		roles.add(roleDao.findById(User.ID_USER_ROLE).orElse(null));
		user.setRoles(roles);
        userDao.save(user);
	}

	@Override
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public boolean isEmailValid(User user) {
		return !userDao.existsByEmail(user.getEmail());
	}

	@Override
	public boolean isUsernameValid(User user) {
		return !userDao.existsByUsername(user.getUsername());
	}

	@Override
	@Transactional
	public void updateImg(String img, User user) {
		userDao.updateImg(img, user);
	}

}
