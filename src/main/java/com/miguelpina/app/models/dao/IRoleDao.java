package com.miguelpina.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.miguelpina.app.models.entity.Role;


public interface IRoleDao extends CrudRepository<Role, Long> {

}
