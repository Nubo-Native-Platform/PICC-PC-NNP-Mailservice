package com.nubons.nnpmailservice.business;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nubons.nnpmailservice.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MailJmsReceiverTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private MailJmsReceiver mailJmsReceiver;

    @Test
    @DisplayName("receiveMailMessage - Successfully process queue message")
    void testReceiveMailMessage() throws Exception {
        doNothing().when(emailService).sendmail(101L);

        assertDoesNotThrow(() -> mailJmsReceiver.receiveMailMessage(101L));

        verify(emailService, times(1)).sendmail(101L);
    }
}
