package com.dumbster.smtp;
import com.dumbster.smtp.action.*;
import org.junit.*;
import java.util.Iterator;
import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.mailstores.EMLMailStore;
import com.dumbster.smtp.mailstores.RollingMailStore;
import static org.junit.Assert.*;

public class NewTestCasesTest {

    private MailMessage message;
    private ServerOptions options;

    @Before
    public void setup() {
        this.message = new MailMessageImpl();
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
}
