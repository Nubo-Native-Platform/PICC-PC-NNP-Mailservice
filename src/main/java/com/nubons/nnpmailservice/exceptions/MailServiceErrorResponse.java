package com.nubons.nnpmailservice.exceptions;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Standard error response model for Mail Service")
public class MailServiceErrorResponse {
	
	@Schema(description = "HTTP status code or application error code", example = "400")
	private int code;

	@Schema(description = "Detailed error description message", example = "Bad Request. Mail Text or template name should be present in the request")
	private String message;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS Z", timezone = "IST")
	@Schema(description = "Timestamp when the error occurred", example = "2026-09-03T17:30:00.000 +0530")
	private Timestamp now;
	
	@Schema(description = "Request URL that triggered the error", example = "/api/v1/sendmail")
	private String url;

}

