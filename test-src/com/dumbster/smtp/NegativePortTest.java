package com.dumbster.smtp;

import com.dumbster.smtp.mailstores.RollingMailStore;

import org.junit.*;
import static org.junit.Assert.*;

public class NegativePortTest {

	private ServerOptions options;

	@Test(expected=NumberFormatException.class)
	@Ignore
	public void testNegativePort() {
		String[] args = new String[]{"-1"};
        options = new ServerOptions(args);
	}

}