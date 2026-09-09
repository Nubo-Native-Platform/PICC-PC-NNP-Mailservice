package com.nubons.nnpmailservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nubons.nnpmailservice.exceptions.MailServiceException;
import com.nubons.nnpmailservice.exceptions.MailServiceExceptionHandler;
import com.nubons.nnpmailservice.model.EmailVO;
import com.nubons.nnpmailservice.service.EmailService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class MailServiceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmailService emailService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private MailServiceController mailServiceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mailServiceController)
                .setControllerAdvice(new MailServiceExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/sendmail - Success with text")
    void testSendMailSuccessWithText() throws Exception {
        EmailVO request = new EmailVO();
        request.setFrom("sender@example.com");
        request.setTo("recipient@example.com");
        request.setSubject("Test Subject");
        request.setText("Test message body");

        EmailVO response = new EmailVO();
        response.setEmailId(1L);
        response.setFrom("sender@example.com");
        response.setTo("recipient@example.com");
        response.setSubject("Test Subject");
        response.setMailSendStatus("QUEUED");

        when(emailService.savemail(any(EmailVO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/sendmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.emailId").value(1L))
                .andExpect(jsonPath("$.mailSendStatus").value("QUEUED"));
    }

    @Test
    @DisplayName("POST /api/v1/sendmail - Success with template")
    void testSendMailSuccessWithTemplate() throws Exception {
        EmailVO request = new EmailVO();
        request.setFrom("sender@example.com");
        request.setTo("recipient@example.com");
        request.setSubject("Welcome Subject");
        request.setTemplateName("welcome.ftlh");
        Map<String, String> data = new HashMap<>();
        data.put("name", "John Doe");
        request.setTemplateDataMap(data);

        EmailVO response = new EmailVO();
        response.setEmailId(2L);
        response.setMailSendStatus("QUEUED");

        when(emailService.savemail(any(EmailVO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/sendmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.emailId").value(2L))
                .andExpect(jsonPath("$.mailSendStatus").value("QUEUED"));
    }

    @Test
    @DisplayName("POST /api/v1/sendmail - Validation Error: Missing text and templateName")
    void testSendMailMissingTextAndTemplate() throws Exception {
        EmailVO request = new EmailVO();
        request.setFrom("sender@example.com");
        request.setTo("recipient@example.com");
        request.setSubject("Test Subject");

        mockMvc.perform(post("/api/v1/sendmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Bad Request. Mail Text or template name should be present in the request"));
    }

    @Test
    @DisplayName("POST /api/v1/sendmailWithAttachment - Success")
    void testSendMailWithAttachmentSuccess() throws Exception {
        EmailVO vo = new EmailVO();
        vo.setFrom("billing@example.com");
        vo.setTo("user@example.com");
        vo.setSubject("Invoice attached");
        vo.setText("Please find attached.");

        String emailVoJson = objectMapper.writeValueAsString(vo);

        MockMultipartFile document = new MockMultipartFile(
                "document",
                "invoice.pdf",
                "application/pdf",
                "Dummy PDF content".getBytes()
        );

        EmailVO response = new EmailVO();
        response.setEmailId(10L);
        response.setMailSendStatus("QUEUED");

        when(emailService.savemail(any(EmailVO.class), anyList())).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/sendmailWithAttachment")
                .file(document)
                .param("emailVoJson", emailVoJson))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.emailId").value(10L))
                .andExpect(jsonPath("$.mailSendStatus").value("QUEUED"));
    }

    @Test
    @DisplayName("POST /api/v1/sendmailWithCc - Success")
    void testSendMailWithCcSuccess() throws Exception {
        EmailVO request = new EmailVO();
        request.setFrom("sender@example.com");
        request.setTo("to@example.com");
        request.setCc("cc1@example.com, cc2@example.com");
        request.setSubject("CC Notification");
        request.setText("Notification body");

        EmailVO response = new EmailVO();
        response.setEmailId(20L);
        response.setMailSendStatus("QUEUED");

        when(emailService.savemail(any(EmailVO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/sendmailWithCc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.emailId").value(20L));
    }

    @Test
    @DisplayName("POST /api/v1/sendmailWithCc - Validation Error: Missing CC")
    void testSendMailWithCcMissingCc() throws Exception {
        EmailVO request = new EmailVO();
        request.setFrom("sender@example.com");
        request.setTo("to@example.com");
        request.setSubject("CC Notification");
        request.setText("Notification body");

        mockMvc.perform(post("/api/v1/sendmailWithCc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Bad Request. CC email address(es) should be present in the request"));
    }

    @Test
    @DisplayName("POST /api/v1/sendmailWithCcAndAttachment - Success")
    void testSendMailWithCcAndAttachmentSuccess() throws Exception {
        EmailVO vo = new EmailVO();
        vo.setFrom("sender@example.com");
        vo.setTo("to@example.com");
        vo.setCc("cc@example.com");
        vo.setSubject("Attachment and CC");
        vo.setText("Body text");

        String emailVoJson = objectMapper.writeValueAsString(vo);

        MockMultipartFile document = new MockMultipartFile(
                "document",
                "sample.txt",
                "text/plain",
                "Test content".getBytes()
        );

        EmailVO response = new EmailVO();
        response.setEmailId(30L);
        response.setMailSendStatus("QUEUED");

        when(emailService.savemail(any(EmailVO.class), anyList())).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/sendmailWithCcAndAttachment")
                .file(document)
                .param("emailVoJson", emailVoJson))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.emailId").value(30L));
    }

    @Test
    @DisplayName("GET /api/v1/maildetails/{mail_id} - Success")
    void testGetMailDetailsSuccess() throws Exception {
        EmailVO response = new EmailVO();
        response.setEmailId(42L);
        response.setFrom("sender@example.com");
        response.setTo("recipient@example.com");
        response.setSubject("Details Subject");
        response.setMailSendStatus("Success");

        when(emailService.getMailDetails(42L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/maildetails/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailId").value(42L))
                .andExpect(jsonPath("$.mailSendStatus").value("Success"));
    }

    @Test
    @DisplayName("GET /api/v1/maildetails/{mail_id} - Not Found")
    void testGetMailDetailsNotFound() throws Exception {
        when(emailService.getMailDetails(999L)).thenThrow(new MailServiceException(404, "Email with id : 999 Not Found"));

        mockMvc.perform(get("/api/v1/maildetails/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Email with id : 999 Not Found"));
    }

    @Test
    @DisplayName("GET /api/v1/resendmail/{mail_id} - Success")
    void testResendMailSuccess() throws Exception {
        doNothing().when(emailService).resendmail(100L);

        mockMvc.perform(get("/api/v1/resendmail/100"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/resendmail/{mail_id} - Exception handling")
    void testResendMailException() throws Exception {
        doThrow(new MailServiceException(404, "Email not found")).when(emailService).resendmail(101L);

        mockMvc.perform(get("/api/v1/resendmail/101"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
