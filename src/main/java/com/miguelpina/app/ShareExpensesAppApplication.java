package com.miguelpina.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.miguelpina.app.models.service.IUploadFileSevice;

@SpringBootApplication
public class ShareExpensesAppApplication implements CommandLineRunner {

	@Autowired
	IUploadFileSevice uploadFileService;
	
	public static void main(String[] args) {
		SpringApplication.run(ShareExpensesAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		uploadFileService.init();
	}
}
