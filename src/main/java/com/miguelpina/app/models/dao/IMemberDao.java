package com.miguelpina.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.miguelpina.app.models.entity.Member;
import com.miguelpina.app.models.entity.PaymentMethod;


public interface IMemberDao extends CrudRepository<Member, Long> {
	boolean existsByPaymentMethod(PaymentMethod pm);
}
