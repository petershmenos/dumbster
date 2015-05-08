package com.dumbster.smtp;

import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.MailMessage;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Date;

import org.junit.*;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class MockitoTestCases {

	private SmtpServer server;
	private MailMessage mm;

	private static final int SMTP_PORT = 1081;
	private final String SUBJECT = "Message Subject";
	private final String SERVER = "localhost";
    private final String FROM = "sender@here.com";
    private final String TO = "receiver@there.com";
    private final String BODY = "Test Body";

	@Before
	public void setup(){
		server = mock(SmtpServer.class);
		mm = mock(MailMessage.class);
	}

	@Test
	public void mockServerNoEmails() {
		setupMockServerNoEmailsTest();
		assertEquals(0, server.getEmailCount());
	}

	@Test
	public void mockServerWithEmails() {
		setupMockServerWithEmailsTest();
		sendMessage(SMTP_PORT, FROM, SUBJECT, BODY, TO);
		assertEquals(1, server.getEmailCount());
		assertEquals(SUBJECT, mm.getFirstHeaderValue("Subject"));
		assertEquals("Test Body", mm.getBody());
	}

	/* Helpers */

	private void setupMockServerNoEmailsTest() {
		when(server.getEmailCount()).thenReturn(0);
	}

	private void setupMockServerWithEmailsTest() {
		when(mm.getFirstHeaderValue("Subject")).thenReturn(SUBJECT);
		when(server.getEmailCount()).thenReturn(1);
		when(mm.getBody()).thenReturn("Test Body");
	}

	private void sendMessage(int port, String from, String subject, String body, String to) {
    try {
        Properties mailProps = getMailProperties(port);
        Session session = Session.getInstance(mailProps, null);

        MimeMessage msg = createMessage(session, from, to, subject, body);
        Transport.send(msg);
        } 
    catch (Exception e) {
        e.printStackTrace();
        fail("Unexpected exception: " + e);
        }
    }
}