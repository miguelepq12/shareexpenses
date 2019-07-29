package com.miguelpina.app.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miguelpina.app.models.entity.Event;
import com.miguelpina.app.models.entity.Member;
import com.miguelpina.app.models.service.IEventService;
import com.miguelpina.app.models.service.ILabelService;
import com.miguelpina.app.models.service.IPaymentMethodService;
import com.miguelpina.app.models.service.IUploadFileSevice;
import com.miguelpina.app.models.service.IUserService;

@RestController
@RequestMapping("/api/events")
public class EventController {
	// data:.*;base64
	// name:.*;
	private static final String UPLOAD_IMG = "none";

	@Autowired
	IEventService eventService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ILabelService labelService;

	@Autowired
	private IPaymentMethodService pmService;

	@Autowired
	private IUploadFileSevice uploadFileService;

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {

		Resource recurso = null;

		try {
			recurso = uploadFileService.load(filename, IUploadFileSevice.EVENT_IMAGE);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);

	}

	@GetMapping(value = "")
	public @ResponseBody ResponseEntity<?> index(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "label", defaultValue = "0") Long labelId) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Pageable pageableRequest = PageRequest.of(page, 5);
		Page<Event> events = null;

		if (labelId > 0) {
			events = eventService.findEventsByNameAndLabel(pageableRequest, userService.findByUsername(auth.getName()),
					name, labelService.findById(labelId));
		} else {
			events = eventService.findEventsByName(pageableRequest, userService.findByUsername(auth.getName()), name);
		}

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("events", events.getContent());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/create")
	public ResponseEntity<?> create() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> response = new HashMap<>();

		response.put("labels", labelService.findAllByUser(userService.findByUsername(auth.getName())));
		response.put("pms", pmService.findAllByUser(userService.findByUsername(auth.getName())));

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<?> show(@PathVariable long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Event event = null;
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

		response.put("pms", pmService.findAllByUser(userService.findByUsername(auth.getName())));
		response.put("event", event);
		response.put("debtors", getDebtors(event));
		response.put("creditors", getCreditors(event));

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@PostMapping(value = "")
	public ResponseEntity<?> create(@Valid @RequestBody Event event, BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		Event newEvent = null;

		if (result.hasErrors()) {
			response.put("name",
					result.getFieldError("name") != null ? result.getFieldError("name").getDefaultMessage() : "");
			response.put("amount",
					result.getFieldError("amount") != null ? result.getFieldError("amount").getDefaultMessage() : "");
			response.put("paymentMethod",
					result.getFieldError("paymentMethod") != null? result.getFieldError("paymentMethod").getDefaultMessage(): "");
			response.put("label",
					result.getFieldError("label") != null ? result.getFieldError("label").getDefaultMessage() : "");

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		if (event.getImg().equals(UPLOAD_IMG)) {
			if (!event.getImg().isEmpty()) {

				String uniqueFilename = null;
				String codeBase64 = event.getImg().replace("name:.*;", "");
				String fileName = event.getImg().replace(codeBase64, "");

				try {
					uniqueFilename = uploadFileService.copy(codeBase64, fileName, IUploadFileSevice.EVENT_IMAGE);

					event.setImg(uniqueFilename);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				event.setImg(UPLOAD_IMG + ".png");
			}
		}

		try {
			eventService.saveEvent(event);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMostSpecificCause().getMessage());

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Event>(newEvent, HttpStatus.CREATED);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Event event, @PathVariable long id, BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		Event oldEvent = eventService.findEventById(id);
		Event updatedEvent = null;

		if (result.hasErrors()) {
			response.put("name",
					result.getFieldError("name") != null ? result.getFieldError("name").getDefaultMessage() : "");
			response.put("amount",
					result.getFieldError("amount") != null ? result.getFieldError("amount").getDefaultMessage() : "");
			response.put("paymentMethod",
					result.getFieldError("paymentMethod") != null
							? result.getFieldError("paymentMethod").getDefaultMessage()
							: "");
			response.put("label",
					result.getFieldError("label") != null ? result.getFieldError("label").getDefaultMessage() : "");

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		if (event.getImg().equals(UPLOAD_IMG)) {
			if (!event.getImg().isEmpty()) {

				String uniqueFilename = null;
				String codeBase64 = event.getImg().replace("name:.*;", "");
				String fileName = event.getImg().replace(codeBase64, "");

				try {
					uniqueFilename = uploadFileService.copy(codeBase64, fileName, IUploadFileSevice.EVENT_IMAGE);

					event.setImg(uniqueFilename);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				event.setImg(UPLOAD_IMG + ".png");
			}
		}

		try {
			oldEvent.setName(event.getName());
			oldEvent.setAmount(event.getAmount());
			oldEvent.setImg(event.getImg());
			oldEvent.setLabel(event.getLabel());
			oldEvent.setPaymentMethod(event.getPaymentMethod());

			updatedEvent = eventService.saveEvent(oldEvent);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMostSpecificCause().getMessage());

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Event>(updatedEvent, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/delete/{id}")
	public ResponseEntity<?> deleteEvent(@PathVariable Long id, RedirectAttributes flash) {
		Map<String, Object> response = new HashMap<>();

		if (id > 0) {
			eventService.deleteEvent(id);
			response.put("mensaje", "Evento eliminado exitosamente");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
	}
	
	public List<Member> getCreditors(Event event){
		List<Member> creditors=new ArrayList<>();
		
		for (Member member : event.getMembers()) {
			if ((member.getAmount()-event.individualInput())>0) {
				creditors.add(new Member(member.getName(),member.getAmount()-event.individualInput()));
			}
		}
		
		return creditors;
	}
	
	public List<Member> getDebtors(Event event){
		List<Member> debtors=new ArrayList<>();
		
		for (Member member : event.getMembers()) {
			if ((member.getAmount()-event.individualInput())<0) {
				debtors.add(new Member(member.getName(),-(member.getAmount()-event.individualInput())));
			}
		}
		
		return debtors;
	}
}
