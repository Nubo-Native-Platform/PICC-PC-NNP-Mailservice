package com.nubons.nnpmailservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Email attachment metadata and file model")
public class EmailAttachmentVO {
	
	@Schema(description = "Unique identifier of the attachment record", example = "501")
	private Long attachmentId;
	
	@Schema(description = "Original filename with extension", example = "invoice.pdf")
	private String name;
	
	@Schema(description = "MIME content type of the attachment", example = "application/pdf")
	private String contentType;
	
	@Schema(description = "Uploaded multipart file object")
	private MultipartFile file;
}

