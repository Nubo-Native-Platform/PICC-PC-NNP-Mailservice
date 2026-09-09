package com.nubons.nnpmailservice.controller;

import java.util.ArrayList;
import java.util.List;

import com.nubons.nnpmailservice.exceptions.MailServiceErrorResponse;
import com.nubons.nnpmailservice.utils.LogUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nubons.nnpmailservice.exceptions.MailServiceException;
import com.nubons.nnpmailservice.model.EmailAttachmentVO;
import com.nubons.nnpmailservice.model.EmailVO;
import com.nubons.nnpmailservice.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Mail Service", description = "REST APIs for sending emails (plain text, template-based, with CC, with attachments) and querying email status")
public class MailServiceController {

	private EmailService emailService;

	private ObjectMapper objectMapper;

	@Operation(
		summary = "Send email with attachment",
		description = "Queues an email with a file attachment (multipart/form-data) for asynchronous background delivery."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "202", description = "Email accepted and queued for delivery",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmailVO.class))),
		@ApiResponse(responseCode = "400", description = "Bad Request - Missing email text/template or required fields",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Internal Server Error",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class)))
	})
	@PostMapping(value = "/sendmailWithAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EmailVO> savemailWithAttachment(
			@Parameter(description = "JSON-serialized EmailVO object string", required = true, example = "{\"from\":\"sender@example.com\",\"to\":\"user@example.com\",\"subject\":\"Monthly Report\",\"text\":\"Please find attached monthly report.\"}")
			@RequestParam String emailVoJson,
			@Parameter(description = "Document or file to attach", required = true)
			@RequestParam MultipartFile document)
			throws MailServiceException {
		try {
			EmailVO emailVO = objectMapper.readValue(emailVoJson, EmailVO.class);
			List<EmailAttachmentVO> emailAttachmentVOs = new ArrayList<EmailAttachmentVO>(); 
	
			if ((emailVO.getText() == null || emailVO.getText().isEmpty())
					&& (emailVO.getTemplateName() == null || emailVO.getTemplateName().isEmpty())) {
				throw new MailServiceException(400,
						"Bad Request. Mail Text or template name should present in the request");
			}
			EmailAttachmentVO attachementVO = new EmailAttachmentVO();
			
			attachementVO.setFile(document);
			attachementVO.setContentType(document.getContentType());
			attachementVO.setName(document.getOriginalFilename());
			
			emailAttachmentVOs.add(attachementVO);

			EmailVO emailVORes = emailService.savemail(emailVO, emailAttachmentVOs);
			return new ResponseEntity<EmailVO>(emailVORes, HttpStatus.ACCEPTED);
		} catch (MailServiceException e) {
			throw e;
		} catch (Exception e) {
			log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
			throw new MailServiceException(500, e.getLocalizedMessage());
		}

	}

	@Operation(
		summary = "Send standard email",
		description = "Queues a standard email (plain text or FreeMarker template) for asynchronous background delivery."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "202", description = "Email accepted and queued for delivery",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmailVO.class))),
		@ApiResponse(responseCode = "400", description = "Bad Request - Missing email text/template or required fields",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Internal Server Error",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class)))
	})
	@PostMapping(value = "/sendmail", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmailVO> savemail(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
				description = "Email payload containing sender, recipient, subject, and text/template parameters",
				required = true
			)
			@RequestBody EmailVO emailVO) throws MailServiceException {

		if ((emailVO.getText() == null || emailVO.getText().isEmpty())
				&& (emailVO.getTemplateName() == null || emailVO.getTemplateName().isEmpty())) {
			throw new MailServiceException(400,
					"Bad Request. Mail Text or template name should be present in the request");
		}

		try {
			EmailVO emailVORes = emailService.savemail(emailVO);

			return new ResponseEntity<EmailVO>(emailVORes, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
			throw new MailServiceException(500, e.getLocalizedMessage());
		}

	}

	@Operation(
		summary = "Send email with CC recipient(s)",
		description = "Queues an email with CC recipients (plain text or FreeMarker template) for asynchronous background delivery."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "202", description = "Email accepted and queued for delivery",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmailVO.class))),
		@ApiResponse(responseCode = "400", description = "Bad Request - Missing CC recipient or email text/template",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Internal Server Error",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class)))
	})
	@PostMapping(value = "/sendmailWithCc", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmailVO> savemailWithCc(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
				description = "Email payload containing CC recipient(s), sender, recipient, subject, and content",
				required = true
			)
			@RequestBody EmailVO emailVO) throws MailServiceException {

		if (emailVO.getCc() == null || emailVO.getCc().trim().isEmpty()) {
			throw new MailServiceException(400,
					"Bad Request. CC email address(es) should be present in the request");
		}

		if ((emailVO.getText() == null || emailVO.getText().isEmpty())
				&& (emailVO.getTemplateName() == null || emailVO.getTemplateName().isEmpty())) {
			throw new MailServiceException(400,
					"Bad Request. Mail Text or template name should be present in the request");
		}

		try {
			EmailVO emailVORes = emailService.savemail(emailVO);

			return new ResponseEntity<EmailVO>(emailVORes, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
			throw new MailServiceException(500, e.getLocalizedMessage());
		}

	}

	@Operation(
		summary = "Send email with CC and attachment",
		description = "Queues an email with CC recipients and a file attachment (multipart/form-data) for asynchronous background delivery."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "202", description = "Email accepted and queued for delivery",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmailVO.class))),
		@ApiResponse(responseCode = "400", description = "Bad Request - Missing CC recipient or email text/template",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Internal Server Error",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class)))
	})
	@PostMapping(value = "/sendmailWithCcAndAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<EmailVO> savemailWithCcAndAttachment(
			@Parameter(description = "JSON-serialized EmailVO object string including CC addresses", required = true, example = "{\"from\":\"sender@example.com\",\"to\":\"user@example.com\",\"cc\":\"mgr@example.com\",\"subject\":\"Contract\",\"text\":\"Please find attached contract.\"}")
			@RequestParam String emailVoJson,
			@Parameter(description = "Document or file to attach", required = true)
			@RequestParam MultipartFile document)
			throws MailServiceException {
		try {
			EmailVO emailVO = objectMapper.readValue(emailVoJson, EmailVO.class);
			List<EmailAttachmentVO> emailAttachmentVOs = new ArrayList<EmailAttachmentVO>();

			if (emailVO.getCc() == null || emailVO.getCc().trim().isEmpty()) {
				throw new MailServiceException(400,
						"Bad Request. CC email address(es) should be present in the request");
			}

			if ((emailVO.getText() == null || emailVO.getText().isEmpty())
					&& (emailVO.getTemplateName() == null || emailVO.getTemplateName().isEmpty())) {
				throw new MailServiceException(400,
						"Bad Request. Mail Text or template name should be present in the request");
			}
			EmailAttachmentVO attachementVO = new EmailAttachmentVO();

			attachementVO.setFile(document);
			attachementVO.setContentType(document.getContentType());
			attachementVO.setName(document.getOriginalFilename());

			emailAttachmentVOs.add(attachementVO);

			EmailVO emailVORes = emailService.savemail(emailVO, emailAttachmentVOs);
			return new ResponseEntity<EmailVO>(emailVORes, HttpStatus.ACCEPTED);
		} catch (MailServiceException e) {
			throw e;
		} catch (Exception e) {
			log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
			throw new MailServiceException(500, e.getLocalizedMessage());
		}

	}

	@Operation(
		summary = "Get email details by ID",
		description = "Retrieves stored email metadata, delivery status, timestamps, and error state by unique email ID."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Email details retrieved successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmailVO.class))),
		@ApiResponse(responseCode = "404", description = "Email with the given ID was not found",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Internal Server Error",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class)))
	})
	@GetMapping("/maildetails/{mail_id}")
	public ResponseEntity<EmailVO> getMailDetails(
			@Parameter(description = "Unique ID of the email transaction record", required = true, example = "101")
			@PathVariable long mail_id) throws MailServiceException {

		return new ResponseEntity<EmailVO>(emailService.getMailDetails(mail_id), HttpStatus.OK);

	}

	@Operation(
		summary = "Resend an email by ID",
		description = "Re-queues an existing email transaction for redelivery via the JMS message broker."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Email re-queued successfully for redelivery"),
		@ApiResponse(responseCode = "404", description = "Email with the given ID was not found",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Internal Server Error",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MailServiceErrorResponse.class)))
	})
	@GetMapping("/resendmail/{mail_id}")
	public ResponseEntity<Void> resendMail(
			@Parameter(description = "Unique ID of the email transaction to resend", required = true, example = "101")
			@PathVariable long mail_id) throws MailServiceException {

		emailService.resendmail(mail_id);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}

