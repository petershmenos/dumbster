package com.dumbster.smtp;

import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.mailstores.RollingMailStore;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Date;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;  

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;   
import static org.mockito.Mockito.*; 


@RunWith(MockitoJUnitRunner.class)
public class MockitoTest {

	@Mock
	private SmtpServer server;

	@Mock
	private MailMessage mm;

	@Mock
	private ServerOptions options;

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
		options = mock(ServerOptions.class);
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

	@Test
	public void mockMailMessageHeadersAndBody() {
		setupMockMailMessageHeadersAndBody();
		mm.addHeader("To", "you");
		mm.addHeader("From", "me");
		mm.appendBody("This is the body");
		assertEquals("To: you\nFrom: me\n\nThis is the body\n", mm.toString());
	}

	@Test
	public void mockSetServerOptions() {
		String[] args = new String[]{"1", "--threaded=false"};
		setupMockSetServerOptions();
		assertEquals(1, options.port);
		assertEquals(false, options.threaded);
		assertEquals(true, options.valid);
	}

	/* Setup Helpers */

	private void setupMockServerNoEmailsTest() {
		when(server.getEmailCount()).thenReturn(0);
	}

	private void setupMockServerWithEmailsTest() {
		when(mm.getFirstHeaderValue("Subject")).thenReturn(SUBJECT);
		when(server.getEmailCount()).thenReturn(1);
		when(mm.getBody()).thenReturn("Test Body");
	}

	private void setupMockMailMessageHeadersAndBody() {
		when(mm.toString()).thenReturn("To: you\nFrom: me\n\nThis is the body\n");
	}

	private void setupMockSetServerOptions() {
		when(options.mailStore.getClass()).thenReturn(RollingMailStore.class)
	}

	private Properties getMailProperties(int port) {
        Properties mailProps = new Properties();
        mailProps.setProperty("mail.smtp.host", "localhost");
        mailProps.setProperty("mail.smtp.port", "" + port);
        mailProps.setProperty("mail.smtp.sendpartial", "true");
        return mailProps;
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

    private MimeMessage createMessage(Session session, String from, String to, String subject, String body) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(body);
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        return msg;
    }
}