package com.dumbster.smtp;

import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.action.*;
import org.junit.*;
import java.util.Iterator;
import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.mailstores.EMLMailStore;
import com.dumbster.smtp.mailstores.RollingMailStore;
import com.dumbster.smtp.Response;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Date;
import java.io.IOException;
import static org.junit.Assert.*;

public class NewTestCasesTest {

	private static final int SMTP_PORT = 1081;
	private final String SERVER = "localhost";
    private final String FROM = "sender@here.com";
    private final String TO = "baker32@illinois.edu";
    private final String SUBJECT = "Test Dumbster";
    private final String BODY = "Test Body";
    private final int WAIT_TICKS = 10000;

    private MailMessage message;
    private ServerOptions options;
    private MailStore mailStore;
    private MailStore mailStore2;
    private SmtpServer server;

    @Before
    public void setup() {
        this.message = new MailMessageImpl();
        mailStore = new RollingMailStore();
    }



    /* Message Formatting Utests */

    @Test
    public void testMaxHeaders() {
	    message.addHeader("header1", "value1");
		message.addHeader("header2", "value2");
		message.addHeader("header3", "value3");
		message.addHeader("header4", "value4");
		message.addHeader("header5", "value5");
		message.addHeader("header6", "value6");
		message.addHeader("header7", "value7");
		message.addHeader("header8", "value8");
		message.addHeader("header9", "value9");
		message.addHeader("header10", "value10");
		Iterator<String> it = message.getHeaderNames();
		int i = 0;
		while(it.hasNext()) { i++; it.next(); }
		assertEquals(10, i);
    }

    @Test
    public void testMaxHeadersPlus1() {
	    message.addHeader("header1", "value1");
		message.addHeader("header2", "value2");
		message.addHeader("header3", "value3");
		message.addHeader("header4", "value4");
		message.addHeader("header5", "value5");
		message.addHeader("header6", "value6");
		message.addHeader("header7", "value7");
		message.addHeader("header8", "value8");
		message.addHeader("header9", "value9");
		message.addHeader("header10", "value10");
		message.addHeader("header11", "value11");
		Iterator<String> it = message.getHeaderNames();
		int i = 0;
		while(it.hasNext()) { i++; it.next(); }
		assertEquals(11, i);
    }

    @Test
    public void testAppendBadHeader() {
    	message.addHeader("foo", "bar");
    	message.addHeader("tim", "tam");
    	message.addHeader("zing", "zang");
    	message.appendHeader("tim", " tum");
    	assertEquals("tam tum", message.getFirstHeaderValue("tim"));
    }

    /* Server Options Utests */
    @Test
    public void testNegativePort() {
        String[] args = new String[]{"-1"};
        options = new ServerOptions(args);
        assertEquals(-1, options.port);
        assertEquals(true, options.threaded);
        assertEquals(true, options.valid);
        assertEquals(RollingMailStore.class, options.mailStore.getClass());
    }

    /* Request Utests*/
    @Test
    public void testDataBodyState() {
        Request request = Request.createRequest(SmtpState.DATA_BODY, ".");
        assertEquals(".", request.getClientAction().toString());
    }

    @Test
    public void testDataFromCreateRequest() {
        Request request = Request.createRequest(SmtpState.GREET, "DATA");
        assertEquals("DATA", request.getClientAction().toString());
    }

    @Test
    public void testQuitFromCreateRequest() {
        Request request = Request.createRequest(SmtpState.GREET, "QUIT");
        assertEquals("QUIT", request.getClientAction().toString());
    }

    @Test
    public void testHeloInvalidMessage() {
        Request request = Request.createRequest(null, "HELO");
        assertEquals("EHLO", request.getClientAction().toString());
    }

    // This test increases branch coverage
    @Test
    public void testListFromCreateRequest() {
        Request request = Request.createRequest(SmtpState.GREET, "LIST");
        assertEquals("LIST", request.getClientAction().toString());
    }

    /* Action Tests */
    // This test increases branch coverage
    @Test
    public void testListIndexNegative() {
    	List l = new List("-1");
    	//This Test passes if there is no exception
    	//messageIndex is private, no public method to check its value
    }
    //These two tests increase coverage
	@Test
    public void testResponseInvalidIndex() {
    	List l = new List("-1");
    	Response r = l.response(null, mailStore, null);
    	assertEquals("There are 0 message(s).", r.getMessage());
    }

    @Test
    public void testResponseValidNoMessages() {
    	List l = new List("0");
    	Response r = l.response(null, mailStore, null);
    	assertEquals("There are 0 message(s).", r.getMessage());
    }

    /* SMTP Server Tests */
    //This test increases coverage
    @Test
    public void getMessageTest() {
    	MailMessage[] mm = new MailMessage[10];
    	ServerOptions options = new ServerOptions();
    	options.port = SMTP_PORT;
    	server = SmtpServerFactory.startServer(options);
        server.getMessages();
        assertEquals(0, server.getEmailCount());
        server.stop();
    }


    /* Advanced Test Cases */
    @Test 
    public void testUniqueMessagesWithClear() {
    	ServerOptions options = new ServerOptions();
        options.port = SMTP_PORT;
        server = SmtpServerFactory.startServer(options);

        // Message 1
        sendMessage(SMTP_PORT, FROM, SUBJECT, BODY, TO);
        server.anticipateMessageCountFor(1, WAIT_TICKS);
        assertTrue(server.getEmailCount() == 1);
        MailMessage mm = server.getMessage(0);
        assertEquals("Test Dumbster", mm.getFirstHeaderValue("Subject"));

        // Message 2
        sendMessage(SMTP_PORT, FROM, SUBJECT, "HELLO!", TO);
        server.anticipateMessageCountFor(1, WAIT_TICKS);
        assertTrue(server.getEmailCount() == 2);
        mm = server.getMessage(1);
        assertEquals("HELLO!", mm.getBody());

        // Messages 3-10
        int i;
        for (i = 3; i <= 10; i++)
        {
        	sendMessage(SMTP_PORT, FROM, null, Integer.toString(i), TO);
        	assertTrue(server.getEmailCount() == i);
        }
        server.clearMessages();
        sendMessage(SMTP_PORT, FROM, SUBJECT, BODY, TO);
        assertTrue(server.getEmailCount() == 1);
        server.stop();
    }

    @Test 
    public void testUniqueMessagesMultipleMailStores() {
    	ServerOptions options = new ServerOptions();
        options.port = SMTP_PORT;
        server = SmtpServerFactory.startServer(options);
    	mailStore2 = new RollingMailStore();

    	// First rolling Mail Store Message
    	MailMessage fm = new MailMessageImpl();
    	mailStore2.addMessage(fm);
    	
    	// Messages 1-10
    	int i;
        for (i = 1; i <= 10; i++)
        {
        	sendMessage(SMTP_PORT, FROM, null, Integer.toString(i), TO);
        	addAMessage();
        	assertTrue(server.getEmailCount() == i);
        }

        server.clearMessages();
        sendMessage(SMTP_PORT, FROM, SUBJECT, BODY, TO);
        assertTrue(server.getEmailCount() == 1);
        server.stop();

    }

    /* Final Report Tests */

    /* Increase Method Coverage */
    @Test 
    public void startServerNoInputs() {
        server = SmtpServerFactory.startServer();
        server.getMessages();
        assertEquals(0, server.getEmailCount());
        server.stop();
    }

    @Test 
    public void startServerThrowException() {
        ServerOptions options = new ServerOptions();
        options.port = SMTP_PORT;
        server = SmtpServerFactory.startServer(options);
        try {
            throw new IOException();
        }
        catch() {}
    }

    /* Helpers */

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
        } catch (Exception e) {
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

    private void addAMessage() {
        MailMessage message = new MailMessageImpl();
        mailStore2.addMessage(message);
    }
}
