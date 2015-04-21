package com.dumbster.smtp;
import org.junit.*;
import java.util.Iterator;
import com.dumbster.smtp.MailMessage;
import static org.junit.Assert.*;

public class NewTestCasesTest {

    private MailMessage message;

    @Before
    public void setup() {
        this.message = new MailMessageImpl();
    }

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

}
