package com.miguelpina.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

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

import com.miguelpina.app.models.entity.Label;
import com.miguelpina.app.models.service.IEventService;
import com.miguelpina.app.models.service.ILabelService;
import com.miguelpina.app.models.service.IUserService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api/labels")
public class LabelController {

	@Autowired
	private ILabelService labelService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEventService eventService;
	
	Authentication auth;
	
	@GetMapping(value = {""})
	public ResponseEntity<?> index(@RequestParam(name = "page", defaultValue = "0") int page) {

		auth= SecurityContextHolder.getContext().getAuthentication();

		
		Pageable pageableRequest = PageRequest.of(page, 5);
		Page<Label> labels = labelService.findAllByUser(pageableRequest,userService.findByUsername(auth.getName()));

		Map<String,Object> response=new HashMap<String, Object>();
		response.put("labels", labels.getContent());
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<?> show(@PathVariable long id) {
		
		Label label=null;
		Map<String, Object> response=new HashMap<>();

		if (id > 0) {
			label=labelService.findById(id);
			if (label == null) {
				response.put("mensaje", "La etiqueta no existe");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		} else {
			response.put("mensaje", "ID no valido");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Label>(label,HttpStatus.OK);
	}

	@PostMapping(value = "")
	public ResponseEntity<?> create(@Valid @RequestBody Label label, BindingResult result) {
		Label newLabel=null;
		Map<String, Object> response=new HashMap<>();
		auth= SecurityContextHolder.getContext().getAuthentication();
		
		if (result.hasErrors()) {
			response.put("errors", result.getFieldErrors()
					.stream()
					.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		try {
			label.setUser(userService.findByUsername(auth.getName()));
			newLabel=labelService.save(label);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error",e.getMostSpecificCause().getMessage());
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Label>(newLabel,HttpStatus.CREATED);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<?> update(@PathVariable long id,@Valid @RequestBody Label label,BindingResult result) {
		Label oldLabel=labelService.findById(id);
		Label updatedLabel=null;
		Map<String, Object> response=new HashMap<>();
		
		if (result.hasErrors()) {
			response.put("errors", result.getFieldErrors()
					.stream()
					.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		try {
			oldLabel.setName(label.getName());
			oldLabel.setColor(label.getColor());
			
			updatedLabel=labelService.save(oldLabel);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("error",e.getMostSpecificCause().getMessage());
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Label>(updatedLabel,HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response=new HashMap<>();
		
		if (id > 0) {
			if(!eventService.existsEventsWithLabel(labelService.findById(id))) {
				try {
					labelService.delete(id);
					response.put("mensaje", "Etiqueta eliminada");
				}catch (DataAccessException e) {
					response.put("mensaje", "Error al eliminar en la base de datos");
					response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}else {
				response.put("mensaje", "Etiqueta es usada por un evento");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CONFLICT);
			}
		}

		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
	}
}
