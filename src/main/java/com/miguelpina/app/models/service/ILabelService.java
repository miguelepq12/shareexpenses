package com.miguelpina.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.miguelpina.app.models.entity.Label;
import com.miguelpina.app.models.entity.User;

public interface ILabelService {

	public List<Label> findAllByUser(User user);
	
	public Page<Label> findAllByUser(Pageable pageable,User user);

	public Label findById(Long id);
	
	public void save(Label label);
	
	public void delete(Long id);
}
