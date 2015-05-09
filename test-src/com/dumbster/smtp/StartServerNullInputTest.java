package com.dumbster.smtp;

import com.dumbster.smtp.SmtpServer;

import static org.junit.Assert.*;

public class StartServerNullInputTest {

    private SmtpServer server;

    @Test 
    @Ignore
    public void startServerNoInputs() {
        server = SmtpServerFactory.startServer();
        server.getMessages();
        assertEquals(0, server.getEmailCount());
    }
}