package com.miguelpina.app.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miguelpina.app.models.entity.User;
import com.miguelpina.app.models.service.IUploadFileSevice;
import com.miguelpina.app.models.service.IUserService;

@Controller
@RequestMapping("/user")
@SessionAttributes("user")
public class UserController {

	private static final String UPLOAD_IMG = "none";

	@Autowired
	private IUserService userService;

	@Autowired
	private IUploadFileSevice uploadFileService;

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {

		Resource recurso = null;

		try {
			recurso = uploadFileService.load(filename, IUploadFileSevice.USER_IMAGE);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);

	}
	
	@GetMapping("/profile")
	public String viewProfile(Map<String, Object> model) {
		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		
		model.put("user", userService.findByUsername(auth.getName()));
		return "user/profile";
	}
	
	@PostMapping("/change-pass")
	public String changePass(User user,RedirectAttributes flash, Model model,SessionStatus status) {
		
		userService.save(user);
		status.setComplete();
		flash.addFlashAttribute("success", "Contraseña cambiada");
		return "redirect:/user/profile";
	}
	
	@PostMapping("/change-img")
	public String changeImg(User user,RedirectAttributes flash, Model model,@RequestParam("file") MultipartFile photo,
			SessionStatus status) {

		String uniqueFilename = null;
		
		if (!photo.isEmpty()) {

			try {
				uniqueFilename = uploadFileService.copy(photo, IUploadFileSevice.USER_IMAGE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			uniqueFilename=UPLOAD_IMG + ".png";
		}
		
		userService.updateImg(uniqueFilename, user);
		status.setComplete();
		flash.addFlashAttribute("success", "Imagen cambiada");
		return "redirect:/user/profile";
	}

	
	
}
