package com.nubons.nnpmailservice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nubons.nnpmailservice.config.JmsConfig;
import com.nubons.nnpmailservice.entity.Email;
import com.nubons.nnpmailservice.entity.EmailAttachment;
import com.nubons.nnpmailservice.exceptions.MailServiceException;
import com.nubons.nnpmailservice.model.EmailAttachmentVO;
import com.nubons.nnpmailservice.model.EmailVO;
import com.nubons.nnpmailservice.repository.EmailRepo;
import com.nubons.nnpmailservice.serviceImpl.EmailServiceimpl;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private EmailRepo emailRepo;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private Configuration configuration;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private EmailServiceimpl emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage((Session) null);
    }

    @Test
    @DisplayName("sendSimpleMessage - Success")
    void testSendSimpleMessage() {
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendSimpleMessage("sender@example.com", "to@example.com", "Subject", "Body")
        );
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("savemail (without attachments) - Success")
    void testSaveMailWithoutAttachments() throws Exception {
        EmailVO vo = new EmailVO();
        vo.setFrom("sender@example.com");
        vo.setTo("to@example.com");
        vo.setSubject("Test Subject");
        vo.setText("Test message");

        Email savedEntity = new Email();
        savedEntity.setEmailId(1001L);
        savedEntity.setEmailSendStatus("QUEUED");
        savedEntity.setLastUpdatedDate(new Date());

        when(emailRepo.saveAndFlush(any(Email.class))).thenReturn(savedEntity);

        EmailVO result = emailService.savemail(vo);

        assertNotNull(result);
        assertEquals(1001L, result.getEmailId());
        assertEquals("QUEUED", result.getMailSendStatus());
        verify(jmsTemplate, times(1)).convertAndSend(eq(JmsConfig.REQUEST_QUEUE), eq(1001L));
    }

    @Test
    @DisplayName("savemail (with attachments) - Success")
    void testSaveMailWithAttachments() throws Exception {
        EmailVO vo = new EmailVO();
        vo.setFrom("sender@example.com");
        vo.setTo("to@example.com");
        vo.setSubject("With Attachment");
        vo.setText("See attachment");

        MockMultipartFile file = new MockMultipartFile(
                "document",
                "invoice.pdf",
                "application/pdf",
                "sample-content".getBytes()
        );

        EmailAttachmentVO attachmentVO = new EmailAttachmentVO();
        attachmentVO.setFile(file);
        attachmentVO.setName("invoice.pdf");
        attachmentVO.setContentType("application/pdf");

        List<EmailAttachmentVO> attachmentList = new ArrayList<>();
        attachmentList.add(attachmentVO);

        Email savedEntity = new Email();
        savedEntity.setEmailId(1002L);
        savedEntity.setEmailSendStatus("QUEUED");
        savedEntity.setLastUpdatedDate(new Date());

        when(emailRepo.saveAndFlush(any(Email.class))).thenReturn(savedEntity);

        EmailVO result = emailService.savemail(vo, attachmentList);

        assertNotNull(result);
        assertEquals(1002L, result.getEmailId());
        verify(jmsTemplate, times(1)).convertAndSend(eq(JmsConfig.REQUEST_QUEUE), eq(1002L));
    }

    @Test
    @DisplayName("getMailDetails - Success")
    void testGetMailDetailsSuccess() throws Exception {
        Email email = new Email();
        email.setEmailId(50L);
        email.setFrom("sender@example.com");
        email.setTo("to@example.com");
        email.setSubject("Details Test");
        email.setText("Details Text");
        email.setEmailSendStatus("Success");
        email.setLastUpdatedDate(new Date());

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("name", "John");
        email.setTemplateDataMap(objectMapper.writeValueAsString(dataMap));

        when(emailRepo.findById(50L)).thenReturn(Optional.of(email));

        EmailVO details = emailService.getMailDetails(50L);

        assertNotNull(details);
        assertEquals(50L, details.getEmailId());
        assertEquals("Success", details.getMailSendStatus());
        assertEquals("John", details.getTemplateDataMap().get("name"));
    }

    @Test
    @DisplayName("getMailDetails - Not Found")
    void testGetMailDetailsNotFound() {
        when(emailRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MailServiceException.class, () -> emailService.getMailDetails(999L));
    }

    @Test
    @DisplayName("sendmail - Success with plaintext and CC")
    void testSendMailSuccessPlaintext() throws Exception {
        Email email = new Email();
        email.setEmailId(200L);
        email.setFrom("sender@example.com");
        email.setTo("recipient1@example.com, recipient2@example.com");
        email.setCc("cc1@example.com, cc2@example.com");
        email.setSubject("Plaintext Subject");
        email.setText("Plaintext Body");
        email.setEmailSendStatus("QUEUED");
        email.setEmailAttachments(new ArrayList<>());

        when(emailRepo.findByEmailId(200L)).thenReturn(email);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendmail(200L);

        verify(emailSender, times(1)).send(any(MimeMessage.class));
        verify(emailRepo, times(1)).saveAndFlush(email);
        assertEquals("Success", email.getEmailSendStatus());
    }

    @Test
    @DisplayName("sendmail - Success with FreeMarker Template")
    void testSendMailSuccessTemplate() throws Exception {
        Email email = new Email();
        email.setEmailId(300L);
        email.setFrom("sender@example.com");
        email.setTo("to@example.com");
        email.setSubject("Welcome Subject");
        email.setTemplateName("welcome.ftlh");

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("name", "Jane Doe");
        dataMap.put("userId", "jdoe");
        dataMap.put("nnpName", "Cloud Platform");
        dataMap.put("nnpPortalURL", "https://portal.example.com");
        dataMap.put("nnpEmail", "support@example.com");
        dataMap.put("nnpAdmin", "Admin Team");
        email.setTemplateDataMap(objectMapper.writeValueAsString(dataMap));
        email.setEmailAttachments(new ArrayList<>());

        Template mockTemplate = mock(Template.class);
        when(emailRepo.findByEmailId(300L)).thenReturn(email);
        when(configuration.getTemplate("welcome.ftlh")).thenReturn(mockTemplate);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendmail(300L);

        verify(emailSender, times(1)).send(any(MimeMessage.class));
        assertEquals("Success", email.getEmailSendStatus());
    }

    @Test
    @DisplayName("sendmail - Email ID Not in DB")
    void testSendMailEntityNotFound() {
        when(emailRepo.findByEmailId(404L)).thenReturn(null);

        assertThrows(MailServiceException.class, () -> emailService.sendmail(404L));
    }

    @Test
    @DisplayName("resendmail - Success")
    void testResendMailSuccess() throws Exception {
        emailService.resendmail(500L);

        verify(jmsTemplate, times(1)).convertAndSend(eq(JmsConfig.REQUEST_QUEUE), eq(500L));
    }
}
