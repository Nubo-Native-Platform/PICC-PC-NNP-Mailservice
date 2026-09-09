package com.nubons.nnpmailservice.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nubons.nnpmailservice.entity.Email;
import com.nubons.nnpmailservice.entity.EmailAttachment;
import com.nubons.nnpmailservice.exceptions.MailServiceErrorResponse;
import com.nubons.nnpmailservice.exceptions.MailServiceException;
import com.nubons.nnpmailservice.utils.LogUtils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModelAndEntityTest {

    @Test
    @DisplayName("EmailVO getters, setters, and fields")
    void testEmailVO() {
        EmailVO vo = new EmailVO();
        vo.setEmailId(1L);
        vo.setFrom("from@example.com");
        vo.setFromName("Sender");
        vo.setTo("to@example.com");
        vo.setToName("Receiver");
        vo.setCc("cc@example.com");
        vo.setCcName("CC Name");
        vo.setSubject("Subject");
        vo.setText("Text Body");
        vo.setTemplateName("welcome.ftlh");
        vo.setMailSendStatus("QUEUED");
        Date now = new Date();
        vo.setLastUpdatedDate(now);

        Map<String, String> map = new HashMap<>();
        map.put("key", "val");
        vo.setTemplateDataMap(map);

        assertEquals(1L, vo.getEmailId());
        assertEquals("from@example.com", vo.getFrom());
        assertEquals("Sender", vo.getFromName());
        assertEquals("to@example.com", vo.getTo());
        assertEquals("Receiver", vo.getToName());
        assertEquals("cc@example.com", vo.getCc());
        assertEquals("CC Name", vo.getCcName());
        assertEquals("Subject", vo.getSubject());
        assertEquals("Text Body", vo.getText());
        assertEquals("welcome.ftlh", vo.getTemplateName());
        assertEquals("QUEUED", vo.getMailSendStatus());
        assertEquals(now, vo.getLastUpdatedDate());
        assertEquals("val", vo.getTemplateDataMap().get("key"));
    }

    @Test
    @DisplayName("Email entity and attachments relationship")
    void testEmailEntity() {
        Email email = new Email();
        email.setEmailId(10L);
        email.setFrom("from@example.com");
        email.setFromName("Sender");
        email.setTo("to@example.com");
        email.setToName("Receiver");
        email.setCc("cc@example.com");
        email.setCcName("CC Name");
        email.setSubject("Subject");
        email.setText("Text");
        email.setTemplateName("welcome.ftlh");
        email.setTemplateDataMap("{\"name\":\"test\"}");
        email.setEmailSendStatus("Success");
        Date now = new Date();
        email.setLastUpdatedDate(now);

        EmailAttachment attachment = new EmailAttachment();
        attachment.setAttachmentId(100L);
        attachment.setName("doc.pdf");
        attachment.setContentType("application/pdf");
        byte[] bytes = new byte[]{1, 2, 3};
        attachment.setAttachments(bytes);
        attachment.setEmail(email);

        List<EmailAttachment> list = new ArrayList<>();
        list.add(attachment);
        email.setEmailAttachments(list);

        assertEquals(10L, email.getEmailId());
        assertEquals("from@example.com", email.getFrom());
        assertEquals("Success", email.getEmailSendStatus());
        assertEquals(now, email.getLastUpdatedDate());
        assertNotNull(email.getEmailAttachments());
        assertEquals(1, email.getEmailAttachments().size());
        assertEquals(100L, attachment.getAttachmentId());
        assertEquals("doc.pdf", attachment.getName());
        assertEquals("application/pdf", attachment.getContentType());
        assertArrayEquals(bytes, attachment.getAttachments());
        assertEquals(email, attachment.getEmail());
    }

    @Test
    @DisplayName("LogUtils sanitization")
    void testLogUtilsSanitization() {
        assertNull(LogUtils.sanitizeForLog(null));
        assertEquals("hello world", LogUtils.sanitizeForLog("hello world"));
        assertEquals("hello  world  test", LogUtils.sanitizeForLog("hello \r world \n test"));
    }

    @Test
    @DisplayName("MailServiceErrorResponse model")
    void testMailServiceErrorResponse() {
        MailServiceErrorResponse response = new MailServiceErrorResponse();
        response.setCode(400);
        response.setMessage("Bad Request");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        response.setNow(now);
        response.setUrl("/api/v1/sendmail");

        assertEquals(400, response.getCode());
        assertEquals("Bad Request", response.getMessage());
        assertEquals(now, response.getNow());
        assertEquals("/api/v1/sendmail", response.getUrl());
    }

    @Test
    @DisplayName("MailServiceException getters and constructor")
    void testMailServiceException() {
        MailServiceException ex = new MailServiceException(404, "Entity Not Found");
        assertEquals(404, ex.getCode());
        assertEquals("Entity Not Found", ex.getMessage());
    }
}
