package com.nubons.nnpmailservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nubons.nnpmailservice.service.EmailService;

@SpringBootApplication
public class NnpMailserviceApplication {
	
	@Autowired
	private EmailService emailService ;

	public static void main(String[] args) {
		SpringApplication.run(NnpMailserviceApplication.class, args);
	}
//	
//	@PostConstruct
//	public void sendMail() {
//		emailService.sendSimpleMessage("sandip.chakraborty1204@gmail.com", "Test Message", "This is test Email from Java Mail Sender...");
//	}

}
