package com.miguelpina.app.models.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.miguelpina.app.models.entity.Event;
import com.miguelpina.app.models.entity.Member;
import com.miguelpina.app.models.entity.PaymentMethod;


public interface IMemberDao extends CrudRepository<Member, Long> {
	public List<Member> findByEvent(Event event);
	boolean existsByPaymentMethod(PaymentMethod pm);
}
