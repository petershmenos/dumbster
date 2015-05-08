package com.dumbster.smtp;

import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.MailMessage;

import org.junit.*;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockitoTestCases {

	private SmtpServer server;

	@Before
	public void setup(){
		server = mock(SmtpServer.class);
	}

	@Test
	public void mockServer() {
		assertEquals(0, server.getEmailCount());
	}

	/* Helpers */

	private void setupMockServerTest() {
		when(server.getEmailCount().thenReturn(0));
	}

}