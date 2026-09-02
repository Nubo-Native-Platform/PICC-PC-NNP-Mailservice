package com.nubons.nnpmailservice.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int code ;
	private String message ;
	
	public MailServiceException(int code, String message){
		super(message);
		this.code = code ;
		this.message = message ;	
	}
	
	
	

}
