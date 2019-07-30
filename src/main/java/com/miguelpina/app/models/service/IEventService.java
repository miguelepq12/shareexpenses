package com.miguelpina.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.miguelpina.app.models.entity.Event;
import com.miguelpina.app.models.entity.Label;
import com.miguelpina.app.models.entity.Member;
import com.miguelpina.app.models.entity.PaymentMethod;
import com.miguelpina.app.models.entity.User;


public interface IEventService {

	public List<Event> findEventsByUser(User user);
	
	public Page<Event> findEventsByUser(Pageable pageable,User user);
	
	public List<Member> findMembersByEvent(Long eventId);

	public Event saveEvent(Event event);
	
	public void saveMember(Member member);

	public Event findEventById(Long id);
	
	public Member findMember(Long id);
	
	public Page<Event> findEventsByName(Pageable pageable,User user,String name);
	
	public Page<Event> findEventsByNameAndLabel(Pageable pageable,User user,String name,Label label);

	public void deleteEvent(Long id);
	
	public void deleteMember(Long idMember);
	
	public boolean existsEventsWithPm(PaymentMethod pm);
	
	public boolean existsEventsWithLabel(Label label);
	
	public boolean existsMembersWithPm(PaymentMethod pm);
}
