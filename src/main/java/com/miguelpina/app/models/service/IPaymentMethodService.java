package com.miguelpina.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.miguelpina.app.models.entity.Event;
import com.miguelpina.app.models.entity.PaymentMethod;
import com.miguelpina.app.models.entity.User;

public interface IPaymentMethodService {

	public List<PaymentMethod> findAllByUser(User user);
	
	public Page<PaymentMethod> findAllByUser(Pageable pageable,User user);

	public PaymentMethod findById(Long id);
	
	public PaymentMethod save(PaymentMethod pm);

	public void delete(Long id);

	public List<PaymentMethod> findPaymentMethodByMemberOfEvent(Event event);
}
