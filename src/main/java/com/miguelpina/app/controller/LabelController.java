package com.miguelpina.app.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miguelpina.app.models.entity.Label;
import com.miguelpina.app.models.service.IEventService;
import com.miguelpina.app.models.service.ILabelService;
import com.miguelpina.app.models.service.IUserService;
import com.miguelpina.app.util.paginator.PageRender;

@RequestMapping("/labels")
@SessionAttributes("label")
@Controller
public class LabelController {

	@Autowired
	private ILabelService labelService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEventService eventService;

	@GetMapping(value = {""})
	public String list(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		Authentication auth= SecurityContextHolder.getContext().getAuthentication();

		
		Pageable pageableRequest = PageRequest.of(page, 5);
		Page<Label> labels = labelService.findAllByUser(pageableRequest,userService.findByUsername(auth.getName()));

		PageRender<Label> render = new PageRender<>("/labels", labels);

		model.addAttribute("user",userService.findByUsername(auth.getName()));
		model.addAttribute("titulo", "Etiquetas");
		model.addAttribute("labels", labels);
		model.addAttribute("page", render);
		
		return "label/list";
	}

	@RequestMapping(value = "/create")
	public String create(Map<String, Object> model) {

		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		
		model.put("user",userService.findByUsername(auth.getName()));
		model.put("label", new Label(userService.findByUsername(auth.getName())));
		model.put("text_btn", "Agregar");
		model.put("titulo","Crear etiqueta");

		return "label/form";
	}


	@RequestMapping(value = "/create/{id}")
	public String edit(@PathVariable long id, Map<String, Object> model, RedirectAttributes flash) {
		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		Label label=null;

		if (id > 0) {
			label=labelService.findById(id);
			if (label == null) {
				flash.addFlashAttribute("error", "La etiqueta no existe");
				return "redirect:/labels";
			}
		} else {
			flash.addFlashAttribute("error", "Etiqueta no valida");
			return "redirect:/labels";
		}

		model.put("user",userService.findByUsername(auth.getName()));
		model.put("label", label);
		model.put("text_btn", "Modificar");
		model.put("titulo","Modificar etiqueta");

		return "label/form";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String save(@Valid Label label, BindingResult result, RedirectAttributes flash, Model model,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", (label.getId() != null) ? "Modificar etiqueta":"Crear etiqueta");
			return "label/form";
		}

		String msjFlash = (label.getId() != null) ? "Etiqueta modificada" :"Etiqueta creada";

		labelService.save(label);
		status.setComplete();
		flash.addFlashAttribute("success", msjFlash);
		return "redirect:/labels";
	}

	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes flash) {
		if (id > 0) {
			if(!eventService.existsEventsWithLabel(labelService.findById(id))) {
				labelService.delete(id);
				flash.addFlashAttribute("success", "Etiqueta eliminada");
			}else {
				flash.addFlashAttribute("warning", "Etiqueta es usada por un evento");
				return "redirect:/labels";
			}
			
		}

		return "redirect:/labels";
	}
}
