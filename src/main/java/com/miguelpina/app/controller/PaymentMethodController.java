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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miguelpina.app.models.entity.PaymentMethod;
import com.miguelpina.app.models.service.IEventService;
import com.miguelpina.app.models.service.IPaymentMethodService;
import com.miguelpina.app.models.service.IUserService;
import com.miguelpina.app.util.paginator.PageRender;

@RequestMapping("/pms")
@SessionAttributes("pm")
@Controller
public class PaymentMethodController {
	@Autowired
	private IPaymentMethodService paymentMethodService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IEventService eventService;

	@GetMapping(value = {""})
	public String list(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		Authentication auth= SecurityContextHolder.getContext().getAuthentication();

		Pageable pageableRequest = PageRequest.of(page, 5);
		Page<PaymentMethod> paymentMethods = paymentMethodService.findAllByUser(pageableRequest,userService.findByUsername(auth.getName()));

		PageRender<PaymentMethod> render = new PageRender<>("/pms", paymentMethods);

		model.addAttribute("user",userService.findByUsername(auth.getName()));
		model.addAttribute("titulo", "Metodos de pago");
		model.addAttribute("pms", paymentMethods);
		model.addAttribute("page", render);
		
		return "pm/list";
	}

	@RequestMapping(value = "/create")
	public String create(Map<String, Object> model) {
		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		
		model.put("user",userService.findByUsername(auth.getName()));
		model.put("pm", new PaymentMethod(userService.findByUsername(auth.getName())));
		model.put("text_btn", "Agregar");
		model.put("titulo","Crear metodo de pago");

		return "pm/form";
	}


	@RequestMapping(value = "/create/{id}")
	public String edit(@PathVariable long id, Map<String, Object> model, RedirectAttributes flash) {
		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		PaymentMethod pm=null;

		if (id > 0) {
			pm=paymentMethodService.findById(id);
			if (pm == null) {
				flash.addFlashAttribute("error", "El metodo de pago no existe");
				return "redirect:/pms";
			}
		} else {
			flash.addFlashAttribute("error", "Metodo de pago no valido");
			return "redirect:/pms";
		}

		model.put("user",userService.findByUsername(auth.getName()));
		model.put("pm", pm);
		model.put("text_btn", "Modificar");
		model.put("titulo","Modificar metodo de pago");

		return "pm/form";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String save(@Valid @ModelAttribute("pm") PaymentMethod pm, BindingResult result, RedirectAttributes flash, Model model,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", (pm.getId() != null) ? "Modificar metodo de pago":"Crear metodo de pago");
			return "pm/form";
		}


		String msjFlash = (pm.getId() != null) ? "Metodo de pago modificado" :"Metodo de pago creado";


		paymentMethodService.save(pm);
		status.setComplete();
		flash.addFlashAttribute("success", msjFlash);
		return "redirect:/pms";
	}

	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes flash) {
		if (id > 0) {
			PaymentMethod pm=paymentMethodService.findById(id);
			if(!eventService.existsEventsWithPm(pm)
					&&!eventService.existsMembersWithPm(pm)) {
				paymentMethodService.delete(id);
				flash.addFlashAttribute("success", "Metodo de pago eliminado");
			}else {
				flash.addFlashAttribute("warning", "Metodo de pago es usado por un evento o miembro");
				return "redirect:/pms";
			}
			
		}

		return "redirect:/pms";
	}
}
