package org.didelphis.utilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerTest {

	private static OutputStream stream;
	private static StringBuilder stringBuilder = new StringBuilder();
	
	@BeforeAll
	static void init() {
		
		stream  = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				stringBuilder.append((char) b);
			}
		};
		
		Logger.addAppender(stream);
	}
	
	@AfterEach
	void cleanup() {
		stringBuilder = new StringBuilder();
	}
	
	@Test
	void testLoggerMessageOnly() {
		String message = "Test message only";

		Logger logger = Logger.create(LoggerTest.class);
		logger.error(message);
		
		assertTrue(stringBuilder.toString().contains(message));
	}
	
}
