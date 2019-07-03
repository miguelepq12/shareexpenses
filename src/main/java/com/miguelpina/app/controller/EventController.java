package com.miguelpina.app.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miguelpina.app.models.entity.Event;
import com.miguelpina.app.models.entity.Member;
import com.miguelpina.app.models.service.IEventService;
import com.miguelpina.app.models.service.ILabelService;
import com.miguelpina.app.models.service.IPaymentMethodService;
import com.miguelpina.app.models.service.IUploadFileSevice;
import com.miguelpina.app.models.service.IUserService;

@RequestMapping("/events")
@SessionAttributes({"event","member"})
@Controller
public class EventController {

	private static final String UPLOAD_IMG="none";
	
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
			recurso = uploadFileService.load(filename,IUploadFileSevice.EVENT_IMAGE);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);

	}
	
	@GetMapping(value="/rest/list")
	public @ResponseBody List<Event> listEvents(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "label", defaultValue = "0") Long labelId){	
		
		System.out.println(page+" "+name+" "+labelId);
		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		
		Pageable pageableRequest = PageRequest.of(page, 5);
		Page<Event> events =null;
		
		if(labelId>0) {
			events = eventService.findEventsByNameAndLabel(pageableRequest, userService.findByUsername(auth.getName()), name,labelService.findById(labelId));
		}else {
			events = eventService.findEventsByName(pageableRequest, userService.findByUsername(auth.getName()), name);
		}
		System.out.println(events);
		return events.getContent();
	}
	
	
	@RequestMapping(value = "/create")
	public String create(Map<String, Object> model) {
		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		
		model.put("user",userService.findByUsername(auth.getName()));
		model.put("labels", labelService.findAllByUser(userService.findByUsername(auth.getName())));
		model.put("pms", pmService.findAllByUser(userService.findByUsername(auth.getName())));
		model.put("event", new Event(userService.findByUsername(auth.getName())));
		model.put("text_btn", "Agregar");
		model.put("titulo","Crear evento");

		return "event/form";
	}
	
	
	@RequestMapping(value = "/create/{id}")
	public String edit(@PathVariable long id, Map<String, Object> model, RedirectAttributes flash) {
		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		Event event=null;

		if (id > 0) {
			event=eventService.findEventById(id);
			if (event == null) {
				flash.addFlashAttribute("error", "El evento no existe");
				return "redirect:/";
			}
		} else {
			flash.addFlashAttribute("error", "Evento no valido");
			return "redirect:/";
		}

		model.put("user",userService.findByUsername(auth.getName()));
		model.put("labels", labelService.findAllByUser(userService.findByUsername(auth.getName())));
		model.put("pms", pmService.findAllByUser(userService.findByUsername(auth.getName())));
		model.put("event", event);
		model.put("text_btn", "Modificar");
		model.put("titulo","Modificar evento: "+event.getName());

		return "event/form";
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String save(@Valid @ModelAttribute("event") Event event, BindingResult result, RedirectAttributes flash, Model model,
			@RequestParam("file") MultipartFile photo,SessionStatus status) {

		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		
		if (result.hasErrors()) {
			model.addAttribute("user",userService.findByUsername(auth.getName()));
			model.addAttribute("labels", labelService.findAllByUser(userService.findByUsername(auth.getName())));
			model.addAttribute("pms", pmService.findAllByUser(userService.findByUsername(auth.getName())));
			model.addAttribute("text_btn", (event.getId() != null) ?"Modificar":"Agregar");
			model.addAttribute("titulo", (event.getId() != null) ? "Modificar evento: "+event.getName():"Crear evento");
			return "event/form";
		}
		
		if(event.getImg().equals(UPLOAD_IMG)) {
			if (!photo.isEmpty()) {

				if (event.getId() != null && event.getId() > 0 && event.getImg()!= null
						&& event.getImg().length() > 0) {
					uploadFileService.delete(event.getImg(),IUploadFileSevice.EVENT_IMAGE);
				}

				String uniqueFilename = null;

				try {
					uniqueFilename = uploadFileService.copy(photo,IUploadFileSevice.EVENT_IMAGE);
					event.setImg(uniqueFilename);
				} catch (IOException e) {e.printStackTrace();}
			}else {
				event.setImg(UPLOAD_IMG+".png");
			}
		}
		
		
		if(event.getId() != null) {
			String msjFlash = "Evento modificado";
			eventService.saveEvent(event);
			status.setComplete();
			flash.addFlashAttribute("success", msjFlash);
			return "redirect:/events/"+event.getId();
		}else {
			eventService.saveEvent(event);
			status.setComplete();
			return "redirect:/events/"+event.getId()+"/members";
		}

	}
	
	
	@GetMapping(value = "/{id}")
	public String getEvent(@PathVariable("id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		
		Event event=eventService.findEventById(id);
		if (event == null) {
			flash.addFlashAttribute("error","El evento no existe");
			return "redirect:/";
		}

		model.put("user",userService.findByUsername(auth.getName()));
		model.put("pms", pmService.findPaymentMethodByMemberOfEvent(event));
		model.put("event", event);
		model.put("titulo","Evento: "+ event.getName());

		return "event/item";
	}
	
	@GetMapping(value = "/{id}/members")
	public String addMembers(@PathVariable("id") Long idEvent, Model model, RedirectAttributes flash) {

		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		Event event=eventService.findEventById(idEvent);
		
		if (event == null) {
			flash.addFlashAttribute("error","El evento no existe");
			return "redirect:/";
		}


		Member m=new Member("", 0, event.getPaymentMethod(), event);
		
		model.addAttribute("user",userService.findByUsername(auth.getName()));
		model.addAttribute("pms", pmService.findAllByUser(userService.findByUsername(auth.getName())));
		model.addAttribute("member", m);
		model.addAttribute("event", event);
		model.addAttribute("titulo", "Miembros de "+event.getName());

		return "event/members";
	}
	
	@PostMapping(value = "/members/add")
	public String addMember(@Valid @ModelAttribute("member") Member member,RedirectAttributes flash, Model model) {
		
		eventService.saveMember(member);
		
		flash.addFlashAttribute("success", "Miembro agregado");
		return "redirect:/events/"+member.getEvent().getId()+"/members";
	}
	
	@PostMapping(value = "/members/change")
	public String changeMember(@Valid @ModelAttribute("event") Event event,RedirectAttributes flash, Model model) {
		
		eventService.saveEvent(event);
		
		return "redirect:/events/"+event.getId();
	}
	
	@RequestMapping(value = "/members/delete/{id}")
	public String deleteMember(@PathVariable Long id, RedirectAttributes flash) {
		Event event=eventService.findMember(id).getEvent();
		
		if (id > 0) {
			eventService.deleteMember(id);
		}

		return "redirect:/events/"+event.getId()+"/members";
	}
	
	@RequestMapping(value = "/delete/{id}")
	public String deleteEvent(@PathVariable Long id, RedirectAttributes flash) {
		if (id > 0) {
			eventService.deleteEvent(id);
			flash.addFlashAttribute("success", "Evento eliminado exitosamente");
		}

		return "redirect:/";
	}
	
}
