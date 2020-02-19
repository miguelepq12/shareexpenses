package com.miguelpina.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.miguelpina.app.models.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miguelpina.app.models.entity.PaymentMethod;
import com.miguelpina.app.models.service.IEventService;
import com.miguelpina.app.models.service.IPaymentMethodService;
import com.miguelpina.app.models.service.IUserService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RequestMapping("/api/pms")
@RestController
public class PaymentMethodController {
	@Autowired
	private IPaymentMethodService paymentMethodService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEventService eventService;

	Authentication auth;
	
	@GetMapping(value = {""})
	public ResponseEntity<?> index(@RequestParam(name = "page", defaultValue = "0") int page) {

		auth= SecurityContextHolder.getContext().getAuthentication();

		Pageable pageableRequest = PageRequest.of(page, 5);
		Page<PaymentMethod> paymentMethods = paymentMethodService.findAllByUser(pageableRequest,userService.findByUsername(auth.getName()));

		Map<String,Object> response=new HashMap<String, Object>();
		response.put("pms", paymentMethods.getContent());
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}

	@GetMapping(value = {"/totalevent/{id}"})
	public ResponseEntity<?> totalForEvent(@PathVariable long id) {
		Event event = null;
		auth= SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> response = new HashMap<>();

		if (id > 0) {
			event = eventService.findEventById(id);
			if (event == null) {
				response.put("mensaje", "El evento no existe");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		} else {
			response.put("mensaje", "ID no valido");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		Map<String,Object> paymentMethodsAmountTotal;
		List<Map<String,Object>> pmTotal= new ArrayList<>();
		for (PaymentMethod pm: paymentMethodService.findPaymentMethodByMemberOfEvent(event)){
			paymentMethodsAmountTotal=new HashMap<>();
			paymentMethodsAmountTotal.put("name",pm.getName());
			paymentMethodsAmountTotal.put("amount",event.calcAmountForPaymentMethod(pm));
			pmTotal.add(paymentMethodsAmountTotal);
		}

		return new ResponseEntity<>(pmTotal,HttpStatus.OK);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<?> show(@PathVariable long id) {
		
		PaymentMethod pm=null;
		Map<String, Object> response=new HashMap<>();

		if (id > 0) {
			pm=paymentMethodService.findById(id);
			if (pm == null) {
				response.put("mensaje", "La etiqueta no existe");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} else {
			response.put("mensaje", "ID no valido");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<PaymentMethod>(pm,HttpStatus.OK);
	}

	@PostMapping(value = "")
	public ResponseEntity<?> create(@Valid @RequestBody PaymentMethod pm, BindingResult result) {
		PaymentMethod newPm=null;
		Map<String, Object> response=new HashMap<>();
		auth= SecurityContextHolder.getContext().getAuthentication();
		
		if (result.hasErrors()) {
			response.put("errors", result.getFieldErrors()
					.stream()
					.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		try {
			pm.setUser(userService.findByUsername(auth.getName()));
			newPm=paymentMethodService.save(pm);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error",e.getMostSpecificCause().getMessage());
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<PaymentMethod>(newPm,HttpStatus.OK);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<?> update(@PathVariable long id,@Valid @RequestBody PaymentMethod pm,BindingResult result) {
		PaymentMethod oldPm=paymentMethodService.findById(id);
		PaymentMethod updatedPm=null;
		Map<String, Object> response=new HashMap<>();
		
		if (result.hasErrors()) {
			response.put("errors", result.getFieldErrors()
					.stream()
					.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		try {
			oldPm.setName(pm.getName());
			
			updatedPm=paymentMethodService.save(oldPm);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("error",e.getMostSpecificCause().getMessage());
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<PaymentMethod>(updatedPm,HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response=new HashMap<>();
		
		if (id > 0) {
			if(!eventService.existsMembersWithPm(paymentMethodService.findById(id))) {
				try {
					paymentMethodService.delete(id);
					response.put("mensaje", "Metodo de pago eliminado");
				}catch (DataAccessException e) {
					response.put("mensaje", "Error al eliminar en la base de datos");
					response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}else {
				response.put("mensaje", "Metodo de pago es usado por un  miembro");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CONFLICT);
			}
		}

		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
	}
}
