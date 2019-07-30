package com.miguelpina.app.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miguelpina.app.models.entity.User;
import com.miguelpina.app.models.service.IUploadFileSevice;
import com.miguelpina.app.models.service.IUserService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final String UPLOAD_IMG = "none";

	@Autowired
	private IUserService userService;

	@Autowired
	private IUploadFileSevice uploadFileService;
	
	Authentication auth;

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
	public ResponseEntity<?> show() {
		Authentication auth= SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> response=new HashMap<>();
		
		response.put("user", userService.findByUsername(auth.getName()));
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@PutMapping("/pass")
	public ResponseEntity<?> changePass(@RequestBody User user) {
		auth= SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> response=new HashMap<>();
		
		User oldUser=userService.findByUsername(auth.getName());
		
		try {
			oldUser.setPass(user.getPass());
			userService.save(oldUser);
			response.put("mensaje", "Contraseña actualizada con exito");
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar contraseña en la base de datos");
			response.put("error",e.getMostSpecificCause().getMessage());
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@PutMapping("/img")
	public ResponseEntity<?> changeImg(@RequestBody User user) {
		auth= SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> response=new HashMap<>();
		User oldUser=userService.findByUsername(auth.getName());
		
		if (user.getProfileImg()!=null&&!user.getProfileImg().isEmpty()) {

			String uniqueFilename = null;
			String codeBase64 = user.getProfileImg().replaceAll("name:.*;", "");
			String fileName = user.getProfileImg().replace(codeBase64, "").replace("name:","").replace(";","");

			try {
				uniqueFilename = uploadFileService.copy(codeBase64, fileName, IUploadFileSevice.USER_IMAGE);

				oldUser.setProfileImg(uniqueFilename);
			} catch (IOException e) {
				e.printStackTrace();
				response.put("mensaje", "Error al cambiar imagen");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR); 
			}
		} else {
			oldUser.setProfileImg(UPLOAD_IMG + ".png");
		}
		
		User updateUser=userService.save(oldUser);
		response.put("mensaje", "Imagen cambiada");
		response.put("user", updateUser);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK); 
	}

	
	
}
