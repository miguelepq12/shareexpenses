package com.miguelpina.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.miguelpina.app.auth.service.JWTService;
import com.miguelpina.app.models.entity.User;
import com.miguelpina.app.models.service.IUploadFileSevice;
import com.miguelpina.app.models.service.IUserService;

@RestController
public class LoginController {

	private static final String UPLOAD_IMG = "none";

	@Autowired
	private IUserService userService;

	@Autowired
	private IUploadFileSevice uploadFileService;
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/api/registration")
	public ResponseEntity<?> registration(@Valid @RequestBody User user, BindingResult result) {
		Map<String, Object> response=new HashMap<String, Object>();
		
		if (!userService.isEmailValid(user)) {
			response.put("email", "El email ya existe");
		}

		if (!userService.isUsernameValid(user)) {
			response.put("username", "El nombre de usuario ya existe");
		}

		if (result.hasErrors()) {
			response.put("mensaje", "Datos invalidos para registrarse");
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		if (!user.getProfileImg().isEmpty()) {

			String uniqueFilename = null;
			String codeBase64 = user.getProfileImg().replace("name:.*;", "");
			String fileName = user.getProfileImg().replace(codeBase64, "");

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

		User newUser=null;
		
		try {
			newUser=userService.save(user);
		}catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error",e.getMostSpecificCause().getMessage());
			
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		

		response.put("token", getToken(user.getUsername(), user.getPass()));
		response.put("user", newUser);
		response.put("mensaje", "Registrado con exito");
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	
	}
	
	private String getToken(String username,String password) {
		String token="";
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				username, password);

		try {
			token= jwtService.create(authenticationManager.authenticate(authToken));
		} catch (AuthenticationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return token;
	}
}
