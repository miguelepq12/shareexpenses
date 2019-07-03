package com.miguelpina.app.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miguelpina.app.models.entity.Label;
import com.miguelpina.app.models.entity.User;
import com.miguelpina.app.models.service.ILabelService;
import com.miguelpina.app.models.service.IUploadFileSevice;
import com.miguelpina.app.models.service.IUserService;
import com.miguelpina.app.models.service.SecurityServiceImp;

@Controller
public class LoginController {

	private static final String UPLOAD_IMG = "none";

	@Autowired
	private IUserService userService;

	@Autowired
	private ILabelService labelService;

	@Autowired
	private SecurityServiceImp securityService;

	@Autowired
	private IUploadFileSevice uploadFileService;

	@GetMapping("/registration")
	public String registration(Map<String, Object> model) {
		model.put("user", new User());
		model.put("titulo", "Registrate");

		return "registration";
	}

	@PostMapping("/registration")
	public String registration(@Valid User user, BindingResult bindingResult, RedirectAttributes flash, Model model,
			SessionStatus status, @RequestParam("file") MultipartFile photo) {

		if (!userService.isEmailValid(user)) {
			FieldError emailDuplicate = new FieldError("user", "email", user.getEmail(), false,
					new String[] { "El email ya existe" }, new Object[] {}, "El email ya existe");
			bindingResult.addError(emailDuplicate);
		}

		if (!userService.isUsernameValid(user)) {
			FieldError usernameDuplicate = new FieldError("user", "username", user.getUsername(), false,
					new String[] { "El nombre de usuario ya existe" }, new Object[] {},
					"El nombre de usuario ya existe");
			bindingResult.addError(usernameDuplicate);
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("titulo", "Registrate");
			return "registration";
		}
		
		if (!photo.isEmpty()) {

			String uniqueFilename = null;

			try {
				uniqueFilename = uploadFileService.copy(photo, IUploadFileSevice.USER_IMAGE);
				user.setProfileImg(uniqueFilename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			user.setProfileImg(UPLOAD_IMG + ".png");
		}

		userService.save(user);
		securityService.autoLogin(user.getUsername(), user.getPass());
		status.setComplete();
		flash.addFlashAttribute("success", "Registrado con exito");

		return "redirect:/";
	}

	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model, Principal principal,
			RedirectAttributes flash) {

		if (principal != null) {
			flash.addFlashAttribute("info", "Ya has iniciado sesión");
			return "redirect:/";
		}

		if (error != null) {
			model.addAttribute("error", "Los datos ingresados son invalidos");
		}

		if (logout != null) {
			model.addAttribute("success", "Has cerrado sesión exitosamente");
		}

		model.addAttribute("titulo", "Login");
		return "login";
	}

	@GetMapping({ "/" })
	public String home(Model model, Authentication authentication) {

		List<Label> labels = labelService.findAllByUser(userService.findByUsername(authentication.getName()));

		model.addAttribute("user", userService.findByUsername(authentication.getName()));
		model.addAttribute("titulo", "Home");
		model.addAttribute("labels", labels);
		model.addAttribute("username", authentication.getName());
		return "home";
	}
}
