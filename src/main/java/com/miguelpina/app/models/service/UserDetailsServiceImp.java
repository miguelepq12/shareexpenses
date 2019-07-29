package com.miguelpina.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miguelpina.app.models.dao.IUserDao;
import com.miguelpina.app.models.entity.Role;
import com.miguelpina.app.models.entity.User;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
	@Autowired
    private IUserDao userDao;
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) {
		User user = userDao.findByUsername(username);
        
		if (user == null) throw new UsernameNotFoundException(username);

		List<GrantedAuthority> authorities=new ArrayList<>();
		for (Role role :  user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
       
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPass(), authorities);
	}

}
