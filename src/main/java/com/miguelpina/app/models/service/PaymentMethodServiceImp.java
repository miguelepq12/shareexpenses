package com.miguelpina.app.models.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miguelpina.app.models.dao.IPaymentMethodDao;
import com.miguelpina.app.models.entity.Event;
import com.miguelpina.app.models.entity.PaymentMethod;
import com.miguelpina.app.models.entity.User;

@Service
public class PaymentMethodServiceImp implements IPaymentMethodService{

	@Autowired
	IPaymentMethodDao paymentMethodDao;

	@Override
	@Transactional(readOnly = true)
	public List<PaymentMethod> findAllByUser(User user) {
		return paymentMethodDao.findByUser(user);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentMethod> findAllByUser(Pageable pageable, User user) {
		return paymentMethodDao.findByUser(user, pageable);
	}

	@Override
	@Transactional
	public void save(PaymentMethod pm) {
		paymentMethodDao.save(pm);
		
	}

	@Override
	@Transactional
	public void delete(Long id) {
		paymentMethodDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentMethod findById(Long id) {
		return paymentMethodDao.findById(id).orElse(null);
	}
	

	@Override
	public List<PaymentMethod> findPaymentMethodByMemberOfEvent(Event event) {
		return paymentMethodDao.findPaymentMethodByMemberOfEvent(event);
	}
	
}
