package com.miguelpina.app.models.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.miguelpina.app.models.entity.Event;
import com.miguelpina.app.models.entity.Label;
import com.miguelpina.app.models.entity.PaymentMethod;
import com.miguelpina.app.models.entity.User;


public interface IEventDao extends PagingAndSortingRepository<Event, Long>{
	
	public List<Event> findByUser(User user);
	
	public Page<Event> findByUserOrderByCreateAtDesc(User user,Pageable pageable);
	
	public Page<Event> findByUserAndNameContainingIgnoreCaseOrderByCreateAtDesc(User user, String name,Pageable pageable);
	
	public Page<Event> findByUserAndLabelAndNameContainingIgnoreCaseOrderByCreateAtDesc(User user, Label label,String name,Pageable pageable);
	
	public boolean existsByLabel(Label label);
}
