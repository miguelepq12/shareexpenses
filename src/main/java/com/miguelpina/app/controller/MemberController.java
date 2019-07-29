package com.miguelpina.app.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miguelpina.app.models.entity.Member;
import com.miguelpina.app.models.service.IEventService;

@RestController
@RequestMapping("/api/members")
public class MemberController {
	
	@Autowired
	IEventService eventService;
	
	@PostMapping(value = "")
	public ResponseEntity<?> create(@Valid @RequestBody Member member) {
		Map<String, Object> response=new HashMap<>();
		
		try {
			eventService.saveMember(member);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error",e.getMostSpecificCause().getMessage());
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Miembro agregado con exito");
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Member member, @PathVariable Long id) {
		Map<String, Object> response=new HashMap<>();
		Member oldMember=eventService.findMember(id);
		
		try {
			oldMember.setName(member.getName());
			oldMember.setAmount(member.getAmount());
			oldMember.setPaymentMethod(member.getPaymentMethod());
			
			eventService.saveMember(member);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("error",e.getMostSpecificCause().getMessage());
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Miembro actualizado con exito");
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		if (id > 0) {
			eventService.deleteMember(id);
			response.put("mensaje", "Miembro eliminado exitosamente");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
	}
}
