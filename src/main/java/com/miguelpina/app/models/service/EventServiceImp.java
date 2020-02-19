package com.miguelpina.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miguelpina.app.models.dao.IEventDao;
import com.miguelpina.app.models.dao.IMemberDao;
import com.miguelpina.app.models.entity.Event;
import com.miguelpina.app.models.entity.Label;
import com.miguelpina.app.models.entity.Member;
import com.miguelpina.app.models.entity.PaymentMethod;
import com.miguelpina.app.models.entity.User;

@Service
public class EventServiceImp implements IEventService {

	@Autowired
	IEventDao eventDao;
	@Autowired
	IMemberDao memberDao;

	@Override
	@Transactional(readOnly = true)
	public List<Event> findEventsByUser(User user) {
		return eventDao.findByUser(user);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Event> findEventsByUser(Pageable pageable, User user) {
		return eventDao.findByUserOrderByCreateAtDesc(user, pageable);
	}

	@Override
	@Transactional
	public Event saveEvent(Event event) {
		return eventDao.save(event);
	}

	@Override
	@Transactional(readOnly = true)
	public Event findEventById(Long id) {
		return eventDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Event> findEventsByName(Pageable pageable, User user, String name) {
		return eventDao.findByUserAndNameContainingIgnoreCaseOrderByCreateAtDesc(user, name, pageable);
	}

	@Override
	@Transactional
	public void deleteEvent(Long id) {
		eventDao.deleteById(id);
	}

	@Override
	@Transactional
	public void deleteMember(Long idMember) {
		memberDao.deleteById(idMember);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Event> findEventsByNameAndLabel(Pageable pageable, User user, String name, Label label) {
		return eventDao.findByUserAndLabelAndNameContainingIgnoreCaseOrderByCreateAtDesc(user, label, name, pageable);
	}

	@Override
	@Transactional
	public void saveMember(Member member) {
		memberDao.save(member);
	}

	@Override
	@Transactional(readOnly = true)
	public Member findMember(Long id) {
		return memberDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsEventsWithLabel(Label label) {
		return eventDao.existsByLabel(label);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsMembersWithPm(PaymentMethod pm) {
		return memberDao.existsByPaymentMethod(pm);
	}

	@Override
	public List<Member> findMembersByEvent(Long eventId) {
		return memberDao.findByEvent(eventDao.findById(eventId).orElse(null));
	}
}
