package com.miguelpina.app.models.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.miguelpina.app.models.entity.Label;
import com.miguelpina.app.models.entity.User;

public interface ILabelDao  extends PagingAndSortingRepository<Label, Long> {

	public List<Label> findByUser(User user);
	
	public Page<Label> findByUser(User user,Pageable pageable);

}
