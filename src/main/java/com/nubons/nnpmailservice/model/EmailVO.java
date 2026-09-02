package com.nubons.nnpmailservice.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVO {
	
		private Long emailId;
		
		@NotNull
		private String from;
		private String fromName;
		@NotNull
		private String to;
		private String toName;
		private String cc;
		private String ccName;
		@NotNull
		private String subject;
		private String text ;
		private String templateName ;
		private Map<String, String> templateDataMap = new HashMap<String, String>();
		
		private String mailSendStatus ;
		
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		private Date lastUpdatedDate ;
		
		
		//private List<EmailAttachmentVO> emailAttachmentVOs= new ArrayList<EmailAttachmentVO>();

}
