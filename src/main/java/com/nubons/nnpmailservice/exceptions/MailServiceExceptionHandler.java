package com.nubons.nnpmailservice.exceptions;

import java.sql.Timestamp;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class MailServiceExceptionHandler {
	
	
	@ExceptionHandler(MailServiceException.class)
	public ResponseEntity<MailServiceErrorResponse> handleMailServiceException(MailServiceException exception, 
			HttpServletRequest request) {
		
		MailServiceErrorResponse error = new MailServiceErrorResponse();
		error.setCode(exception.getCode());
		error.setMessage(exception.getMessage());
		error.setNow(new Timestamp(System.currentTimeMillis()));
		error.setUrl(request.getRequestURI());
		
		
		return new ResponseEntity<MailServiceErrorResponse>(error,HttpStatusCode.valueOf(exception.getCode()));
		
	}

}
