package com.nubons.nnpmailservice.business;



import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nubons.nnpmailservice.config.JmsConfig;
import com.nubons.nnpmailservice.exceptions.MailServiceException;
import com.nubons.nnpmailservice.service.EmailService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class MailJmsReceiver {
	
	private EmailService emailService ;
	
	
	@JmsListener(destination = JmsConfig.REQUEST_QUEUE)
	public void receiveMailMessage(Long mail_id) throws MailServiceException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new MailServiceException(500, e.getMessage());
		}
		// log.info("Received mail_id : " + mail_id + " through JMS");
		
		emailService.sendmail(mail_id);
	}

}
