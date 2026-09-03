package com.nubons.nnpmailservice.serviceImpl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import com.nubons.nnpmailservice.utils.LogUtils;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.nubons.nnpmailservice.config.JmsConfig;
import com.nubons.nnpmailservice.entity.Email;
import com.nubons.nnpmailservice.entity.EmailAttachment;
import com.nubons.nnpmailservice.exceptions.MailServiceException;
import com.nubons.nnpmailservice.model.EmailAttachmentVO;
import com.nubons.nnpmailservice.model.EmailVO;
import com.nubons.nnpmailservice.repository.EmailRepo;
import com.nubons.nnpmailservice.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@AllArgsConstructor
public class EmailServiceimpl implements EmailService {


    private JavaMailSender emailSender;

    private EmailRepo emailRepo;

    private ObjectMapper objectMapper;

    private JmsTemplate jmsTemplate;

    private Configuration configuration;

    @Override
    public void sendSimpleMessage(String emailFrom, String to, String subject, String body) throws RuntimeException {

        // log.info(emailSender.toString());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSentDate(new Date());
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            // log.info(String.format("Sending Email to {%s}", to));
            emailSender.send(message);
            // log.info(String.format("Mail sent to {%s}", to));
        } catch (Exception e) {
            log.error("Error Occurred while sending message to {}", LogUtils.sanitizeForLog(e.getLocalizedMessage()));
        }

    }

    @Override
    @Transactional
    public EmailVO savemail(EmailVO emailVO, List<EmailAttachmentVO> attachmentVOs) throws MailServiceException {

        Email email = new Email();
        email.setFrom(emailVO.getFrom());
        email.setFromName(emailVO.getFromName());
        email.setTo(emailVO.getTo());
        email.setToName(emailVO.getToName());
        email.setCc(emailVO.getCc());
        email.setCcName(emailVO.getCcName());
        email.setText(emailVO.getText());
        email.setTemplateName(emailVO.getTemplateName());
        email.setSubject(emailVO.getSubject());

        email.setEmailSendStatus("QUEUED");
        email.setLastUpdatedDate(new Date());
        
        if(emailVO.getTemplateDataMap()!=null && !emailVO.getTemplateDataMap().isEmpty()) {
        	String templateDataJson;
            try {
                templateDataJson = objectMapper.writeValueAsString(emailVO.getTemplateDataMap());
            } catch (JsonProcessingException e) {
                log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
                throw new MailServiceException(500, e.getLocalizedMessage());
            }

            email.setTemplateDataMap(templateDataJson);
        }

        

        List<EmailAttachment> emailAttachments = new ArrayList<EmailAttachment>();

        for (EmailAttachmentVO attachment : attachmentVOs) {
            EmailAttachment emailAttachment = new EmailAttachment();
            emailAttachment.setName(attachment.getName());
            emailAttachment.setContentType(attachment.getContentType());
            try {
                emailAttachment.setAttachments(attachment.getFile().getBytes());
            } catch (IOException e) {
                log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
                throw new MailServiceException(500, e.getLocalizedMessage());
            }
            emailAttachment.setEmail(email);
            emailAttachments.add(emailAttachment);
        }

        email.setEmailAttachments(emailAttachments);


        // log.info("Saving Email Data in DB : " + email.toString());

        Email savedEmail = emailRepo.saveAndFlush(email);

        // log.info("Successfully saved Email Data in DB : " + savedEmail.toString());


        // log.info("Senidng email_id to JMS queue...");

        try {
            jmsTemplate.convertAndSend(JmsConfig.REQUEST_QUEUE, savedEmail.getEmailId());
        } catch (Exception e) {
            log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
            throw new MailServiceException(500, e.getLocalizedMessage());
        }

        // log.info("Successfully sent email_id to JMS queue...");

        emailVO.setMailSendStatus(savedEmail.getEmailSendStatus());
        emailVO.setLastUpdatedDate(savedEmail.getLastUpdatedDate());
        emailVO.setEmailId(savedEmail.getEmailId());
        emailVO.setCc(savedEmail.getCc());
        emailVO.setCcName(savedEmail.getCcName());
        return emailVO;

    }

    @Override
    @Transactional
    public EmailVO savemail(EmailVO emailVO) throws MailServiceException {

        Email email = new Email();
        email.setFrom(emailVO.getFrom());
        email.setFromName(emailVO.getFromName());
        email.setTo(emailVO.getTo());
        email.setToName(emailVO.getToName());
        email.setCc(emailVO.getCc());
        email.setCcName(emailVO.getCcName());
        email.setText(emailVO.getText());
        email.setTemplateName(emailVO.getTemplateName());
        email.setSubject(emailVO.getSubject());

        email.setEmailSendStatus("QUEUED");
        email.setLastUpdatedDate(new Date());

		if (emailVO.getTemplateDataMap() != null && !emailVO.getTemplateDataMap().isEmpty()) {
			String templateDataJson;
			try {
				templateDataJson = objectMapper.writeValueAsString(emailVO.getTemplateDataMap());
			} catch (JsonProcessingException e) {
				log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
				throw new MailServiceException(500, e.getLocalizedMessage());
			}

			email.setTemplateDataMap(templateDataJson);
		}

        // log.info("Saving Email Data in DB : " + email.toString());

        Email savedEmail = emailRepo.saveAndFlush(email);

        // log.info("Successfully saved Email Data in DB : " + savedEmail.toString());

        // log.info("Senidng email_id to JMS queue...");

        try {
            jmsTemplate.convertAndSend(JmsConfig.REQUEST_QUEUE, savedEmail.getEmailId());
        } catch (Exception e) {
            log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
            throw new MailServiceException(500, e.getLocalizedMessage());
        }

        // log.info("Successfully sent email_id to JMS queue...");

        emailVO.setMailSendStatus(savedEmail.getEmailSendStatus());
        emailVO.setLastUpdatedDate(savedEmail.getLastUpdatedDate());
        emailVO.setEmailId(savedEmail.getEmailId());
        emailVO.setCc(savedEmail.getCc());
        emailVO.setCcName(savedEmail.getCcName());

        return emailVO;

    }

    @Override
    @Transactional
    public EmailVO getMailDetails(long email_id) throws MailServiceException {

        EmailVO emailVO = new EmailVO();
        Email email = emailRepo.findById(email_id).orElse(null);

        if (email == null) {
            throw new MailServiceException(404, "Email with id : " + email_id + " Not Found ");
        }

        emailVO.setFrom(email.getFrom());
        emailVO.setFromName(email.getFromName());
        emailVO.setTo(email.getTo());
        emailVO.setToName(email.getToName());
        emailVO.setCc(email.getCc());
        emailVO.setCcName(email.getCcName());
        emailVO.setSubject(email.getSubject());
        emailVO.setEmailId(email.getEmailId());
        emailVO.setTemplateName(email.getTemplateName());
        emailVO.setText(email.getText());
        emailVO.setMailSendStatus(email.getEmailSendStatus());
        emailVO.setLastUpdatedDate(email.getLastUpdatedDate());

        try {
            Map<String, String> templateDataMap = objectMapper.readValue(email.getTemplateDataMap(), Map.class);
            emailVO.setTemplateDataMap(templateDataMap);
        } catch (Exception e) {
            throw new MailServiceException(500, e.getLocalizedMessage());
        }


        return emailVO;
    }

    @Override
    @Transactional
    public void sendmail(long email_id) throws MailServiceException {

        // log.info(String.format("Received email_id : %s to send email from JMS.", email_id));

        Email email = emailRepo.findByEmailId(email_id);

        if (email==null) {
            log.error("Email with id : {}  not present in DB",email_id);
            throw new MailServiceException(500, "Email with id : " + email_id + " not present in DB");
        }

        //Email email = emailOpt.get();

        // log.info("Sending for ..." + email.toString());

        String mailBody;

        if (email.getTemplateName() != null && !email.getTemplateName().isEmpty()) {

            Map<String, String> templateDataMap = new HashMap<String, String>();
            try {
                templateDataMap = objectMapper.readValue(email.getTemplateDataMap(), Map.class);
            } catch (JsonMappingException e) {
                log.error("Error while parsing template data map|| {}" ,LogUtils.sanitizeForLog( e.getLocalizedMessage()));
                //e.printStackTrace();

                throw new MailServiceException(500, "Error while parsing template data map||" + e.getLocalizedMessage());

            } catch (JsonProcessingException e) {
                log.error("Error while parsing template data map|| {}",LogUtils.sanitizeForLog( e.getLocalizedMessage()));
                //e.printStackTrace();
                throw new MailServiceException(500, "Error while parsing template data map||" + e.getLocalizedMessage());

            }

            try {
                mailBody = getEmailContent(email.getTemplateName(), templateDataMap);
            } catch (IOException e) {
                log.error("Error while creating email body from template|| {}",LogUtils.sanitizeForLog( e.getLocalizedMessage()));
                //e.printStackTrace();

                throw new MailServiceException(500, "Error while creating email body from template||" + e.getLocalizedMessage());
            } catch (TemplateException e) {
                log.error("Error while creating email body from template|| {}",LogUtils.sanitizeForLog( e.getLocalizedMessage()));
                //e.printStackTrace();

                throw new MailServiceException(500, "Error while creating email body from template||" + e.getLocalizedMessage());
            }
        } else {
            mailBody = email.getText();
        }

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(email.getFrom());
            String[] toAddresses = Arrays.stream(email.getTo().split(","))
                                         .map(String::trim)
                                         .filter(s -> !s.isEmpty())
                                         .toArray(String[]::new);
            helper.setTo(toAddresses);

            if (email.getCc() != null && !email.getCc().trim().isEmpty()) {
                String[] ccAddresses = Arrays.stream(email.getCc().split(","))
                                             .map(String::trim)
                                             .filter(s -> !s.isEmpty())
                                             .toArray(String[]::new);
                if (ccAddresses.length > 0) {
                    helper.setCc(ccAddresses);
                }
            }

            helper.setSubject(email.getSubject());
            helper.setText(mailBody,true);
            for (EmailAttachment emailAttachment : email.getEmailAttachments()) {
                ByteArrayDataSource bads = new ByteArrayDataSource(emailAttachment.getAttachments(), emailAttachment.getContentType());
                helper.addAttachment(emailAttachment.getName(), bads);
            }

            emailSender.send(message);

            // log.info(String.format("Email Successfully sent for email_id : %s", email_id));
            email.setEmailSendStatus("Success");
            emailRepo.saveAndFlush(email);
            // log.info(String.format("status changetd to success for email_id : %s", email_id));


        } catch (MessagingException e) {
            log.error("Error while creating Email Message|| {}",LogUtils.sanitizeForLog(e.getLocalizedMessage()));
            //e.printStackTrace();

            throw new MailServiceException(500, "Error while creating Email Message||" + e.getLocalizedMessage());
        }


    }
    private enum EmailTemplate {
        BILLING_INVOICE("billing_invoice.ftl"),
        REPLICATION("replication.ftlh"),
        WELCOME("welcome.ftlh"),
        WELCOME_NEW("welcomeNew.ftlh"),
        WELCOME_STR("welcomeStr.ftlh");

        final String fileName;
        EmailTemplate(String fileName) { this.fileName = fileName; }

        static EmailTemplate fromName(String name) {
            for (EmailTemplate t : values()) {
                if (t.fileName.equals(name)) return t;
            }
            throw new IllegalArgumentException("Invalid email template: " + name);
        }
    }

    private String getEmailContent(String templateName, Map<String, String> templateDataMap)
            throws IOException, TemplateException {

        EmailTemplate resolved = EmailTemplate.fromName(templateName);

        Template template = switch (resolved) {
            case BILLING_INVOICE -> configuration.getTemplate("billing_invoice.ftl");
            case REPLICATION     -> configuration.getTemplate("replication.ftlh");
            case WELCOME          -> configuration.getTemplate("welcome.ftlh");
            case WELCOME_NEW      -> configuration.getTemplate("welcomeNew.ftlh");
            case WELCOME_STR      -> configuration.getTemplate("welcomeStr.ftlh");
        };

        StringWriter stringWriter = new StringWriter();
        template.process(templateDataMap, stringWriter);
        return stringWriter.toString();
    }
    @Override
    public void resendmail(long mail_id) throws MailServiceException {
        // log.info("Resending mail with id : " + mail_id);

        try {
            jmsTemplate.convertAndSend(JmsConfig.REQUEST_QUEUE, mail_id);
        } catch (Exception e) {
            log.error(LogUtils.sanitizeForLog(e.getLocalizedMessage()));
            throw new MailServiceException(500, e.getLocalizedMessage());
        }


    }


}
