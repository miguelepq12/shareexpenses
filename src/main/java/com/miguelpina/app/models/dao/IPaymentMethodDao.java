package com.miguelpina.app.models.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.miguelpina.app.models.entity.Event;
import com.miguelpina.app.models.entity.PaymentMethod;
import com.miguelpina.app.models.entity.User;


public interface IPaymentMethodDao  extends PagingAndSortingRepository<PaymentMethod, Long>{

	public List<PaymentMethod> findByUser(User user);
	
	public Page<PaymentMethod> findByUser(User user,Pageable pageable);
	

	@Query("select distinct m.paymentMethod from Member m inner join m.event e where e=?1 ")
	public List<PaymentMethod>findPaymentMethodByMemberOfEvent(Event event);
	

}
