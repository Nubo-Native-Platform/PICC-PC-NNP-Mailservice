package com.nubons.nnpmailservice.controller;

import java.util.ArrayList;
import java.util.List;

import com.nubons.nnpmailservice.utils.LogUtils;
import org.springframework.http.HttpStatus;
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
public class MailServiceController {

	private EmailService emailService;

	private ObjectMapper objectMapper;

	@PostMapping("/sendmailWithAttachment")
	public ResponseEntity<EmailVO> savemailWithAttachment(@RequestParam String emailVoJson,@RequestParam MultipartFile document)
			throws MailServiceException {
		try {
			EmailVO emailVO = objectMapper.readValue(emailVoJson, EmailVO.class);
			List<EmailAttachmentVO> emailAttachmentVOs= new ArrayList<EmailAttachmentVO>(); 
	
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

			EmailVO emailVORes = emailService.savemail(emailVO,emailAttachmentVOs);
			return new ResponseEntity<EmailVO>(emailVORes, HttpStatus.ACCEPTED);
		} catch (MailServiceException e) {
			throw e;
		} catch (Exception e) {
			log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
			throw new MailServiceException(500, e.getLocalizedMessage());
		}

	}

	@PostMapping("/sendmail")
	public ResponseEntity<EmailVO> savemail(@RequestBody EmailVO emailVO) throws MailServiceException {

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

	@PostMapping("/sendmailWithCc")
	public ResponseEntity<EmailVO> savemailWithCc(@RequestBody EmailVO emailVO) throws MailServiceException {

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

	@PostMapping("/sendmailWithCcAndAttachment")
	public ResponseEntity<EmailVO> savemailWithCcAndAttachment(@RequestParam String emailVoJson, @RequestParam MultipartFile document)
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

	@GetMapping("/maildetails/{mail_id}")
	public ResponseEntity<EmailVO> getMailDetails(@PathVariable long mail_id) throws MailServiceException {

		return new ResponseEntity<EmailVO>(emailService.getMailDetails(mail_id), HttpStatus.OK);

	}

	@GetMapping("/resendmail/{mail_id}")
	public ResponseEntity<Void> resendMail(@PathVariable long mail_id) throws MailServiceException {

		emailService.resendmail(mail_id);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
