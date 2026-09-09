package com.nubons.nnpmailservice.service;

import java.util.List;

import com.nubons.nnpmailservice.exceptions.MailServiceException;
import com.nubons.nnpmailservice.model.EmailAttachmentVO;
import com.nubons.nnpmailservice.model.EmailVO;

public interface EmailService {
	
	public void sendSimpleMessage(String emailFrom,String to, String subject, String body);
	
	public EmailVO savemail(EmailVO emailVO) throws MailServiceException;
	
	public EmailVO savemail(EmailVO emailVO, List<EmailAttachmentVO> attachmentVOs) throws MailServiceException;
	
	public EmailVO getMailDetails(long id) throws MailServiceException ;
	
	public void sendmail(long email_id) throws MailServiceException ;
	
	public void resendmail(long mail_id) throws MailServiceException ;
	
}
