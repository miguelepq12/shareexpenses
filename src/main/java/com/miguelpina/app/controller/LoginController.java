package com.miguelpina.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.miguelpina.app.auth.service.ISecurityService;
import com.miguelpina.app.models.entity.User;
import com.miguelpina.app.models.service.IUploadFileSevice;
import com.miguelpina.app.models.service.IUserService;

@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
public class LoginController {

	private static final String UPLOAD_IMG = "none";

	@Autowired
	private IUserService userService;

	@Autowired
	private IUploadFileSevice uploadFileService;
	
	@Autowired
	private ISecurityService securityService;

	@PostMapping("/api/registration")
	public ResponseEntity<?> registration( @Valid @RequestBody User user, BindingResult result) {
		Map<String, Object> response=new HashMap<String, Object>();
		String password=user.getPass();

		if (!userService.isEmailValid(user)) {
			FieldError emailDuplicate = new FieldError("user", "email", user.getEmail(), false,
					new String[] { "El email ya existe" }, new Object[] {}, "El email ya existe");
			result.addError(emailDuplicate);
		}

		if (!userService.isUsernameValid(user)) {
			FieldError usernameDuplicate = new FieldError("user", "username", user.getUsername(), false,
					new String[] { "El nombre de usuario ya existe" }, new Object[] {},
					"El nombre de usuario ya existe");
			result.addError(usernameDuplicate);
		}

		if (result.hasErrors()) {
			response.put("mensaje", "Datos invalidos para registrarse");
			response.put("errors", result.getFieldErrors()
					.stream()
					.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		if (user.getProfileImg()!=null&&!user.getProfileImg().isEmpty()) {

			String uniqueFilename = null;
			String codeBase64 = user.getProfileImg().replaceAll("name:.*;", "");
			String fileName = user.getProfileImg().replace(codeBase64, "").replace("name:","").replace(";","");

			try {
				uniqueFilename = uploadFileService.copy(codeBase64, fileName, IUploadFileSevice.USER_IMAGE);

				user.setProfileImg(uniqueFilename);
			} catch (IOException e) {
				e.printStackTrace();
				response.put("mensaje", "Error al guardar imagen");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR); 
			}
		} else {
			user.setProfileImg(UPLOAD_IMG + ".png");
		}
		
		try {
			userService.save(user);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error",e.getMostSpecificCause().getMessage());
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		

		String token="";
		
		try {
			token=securityService.autoLogin(user.getUsername(), password);
		} catch (AuthenticationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.put("token", token);
		response.put("user", user);
		response.put("mensaje", "Registrado con exito");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	
	}
}
