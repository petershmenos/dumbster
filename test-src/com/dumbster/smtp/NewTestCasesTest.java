package com.dumbster.smtp;
import com.dumbster.smtp.action.*;
import org.junit.*;
import java.util.Iterator;
import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.mailstores.EMLMailStore;
import com.dumbster.smtp.mailstores.RollingMailStore;
import com.dumbster.smtp.Response;
import static org.junit.Assert.*;

public class NewTestCasesTest {

	private static final int SMTP_PORT = 1081;
    private MailMessage message;
    private ServerOptions options;
    private MailStore mailStore;
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

}
