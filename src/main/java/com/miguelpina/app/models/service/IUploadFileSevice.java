package com.miguelpina.app.models.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileSevice {

	public final static String USER_IMAGE="user";
	public final static String EVENT_IMAGE="event";
	
	public Resource load(String filename,String folder) throws MalformedURLException;
	
	public String copy(MultipartFile file,String folder) throws IOException;
	
	public boolean delete(String filename,String folder);
	
	public void deleteAll();
	
	public void init() throws IOException;
}
