package com.nubons.nnpmailservice.exceptions;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailServiceErrorResponse {
	
	private int code ;
	private String message ;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS Z", timezone = "IST")
	private Timestamp now ;
	
	private String url ;
	

}
