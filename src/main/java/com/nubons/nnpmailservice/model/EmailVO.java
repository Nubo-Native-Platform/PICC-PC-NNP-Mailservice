package com.nubons.nnpmailservice.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Email request/response data transfer object")
public class EmailVO {
	
		@Schema(description = "Unique ID of the email record", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
		private Long emailId;
		
		@NotNull
		@Schema(description = "Sender email address", example = "noreply@nubons.com", requiredMode = Schema.RequiredMode.REQUIRED)
		private String from;

		@Schema(description = "Sender display name", example = "Nubons Notification Service")
		private String fromName;

		@NotNull
		@Schema(description = "Recipient email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
		private String to;

		@Schema(description = "Recipient display name", example = "John Doe")
		private String toName;

		@Schema(description = "Carbon copy (CC) recipient email address(es), comma-separated if multiple", example = "manager@example.com")
		private String cc;

		@Schema(description = "CC recipient display name", example = "Manager")
		private String ccName;

		@NotNull
		@Schema(description = "Email subject line", example = "Your Account Activation Link", requiredMode = Schema.RequiredMode.REQUIRED)
		private String subject;

		@Schema(description = "Plain text email body (either 'text' or 'templateName' must be provided)", example = "Hello, your account has been created.")
		private String text;

		@Schema(description = "FreeMarker template filename (located in /templates/ directory)", example = "welcome.ftlh")
		private String templateName;

		@Schema(description = "Key-value map containing dynamic model variables for template rendering", example = "{\"userName\": \"John Doe\", \"actionUrl\": \"https://example.com/verify\"}")
		private Map<String, String> templateDataMap = new HashMap<String, String>();
		
		@Schema(description = "Status of the email delivery (e.g., QUEUED, SENT, FAILED)", example = "QUEUED", accessMode = Schema.AccessMode.READ_ONLY)
		private String mailSendStatus;
		
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		@Schema(description = "Timestamp of the last update to this email record", example = "2026-09-03 17:30:00", accessMode = Schema.AccessMode.READ_ONLY)
		private Date lastUpdatedDate;
		
		//private List<EmailAttachmentVO> emailAttachmentVOs= new ArrayList<EmailAttachmentVO>();

}

