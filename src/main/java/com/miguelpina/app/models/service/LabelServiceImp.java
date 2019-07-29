package com.miguelpina.app.models.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miguelpina.app.models.dao.ILabelDao;
import com.miguelpina.app.models.entity.Label;
import com.miguelpina.app.models.entity.User;

@Service
public class LabelServiceImp implements ILabelService{

	@Autowired
	ILabelDao labelDao;

	@Override
	@Transactional(readOnly = true)
	public List<Label> findAllByUser(User user) {
		return labelDao.findByUser(user);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Label> findAllByUser(Pageable pageable, User user) {
		return labelDao.findByUser(user, pageable);
	}

	@Override
	@Transactional
	public Label save(Label label) {
		return labelDao.save(label);
		
	}

	@Override
	@Transactional
	public void delete(Long id) {
		labelDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Label findById(Long id) {
		return labelDao.findById(id).orElse(null);
	}
	
	
}
