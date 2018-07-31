package org.didelphis.utilities;

import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerTest {

	private static OutputStream stream;
	private static StringBuilder stringBuilder = new StringBuilder();

	@BeforeAll
	static void init() {
		stream = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				stringBuilder.append((char) b);
			}
		};
		Logger.addAppender(stream);
	}

	@BeforeEach
	void cleanup() {
		stringBuilder = new StringBuilder();
	}

	@Test
	void testLevelError() {
		Logger logger = getLogger();
		logger.error("");
		String buffer = getBuffer();
		assertTrue(buffer.startsWith("[ERROR]"), buffer);
	}

	@Test
	void testLevelWarn() {
		Logger logger = getLogger();
		logger.warn("");
		String buffer = getBuffer();
		assertTrue(buffer.startsWith("[WARN]"), buffer);
	}

	@Test
	void testLevelInfo() {
		Logger logger = getLogger();
		logger.info("");
		String buffer = getBuffer();
		assertTrue(buffer.startsWith("[INFO]"), buffer);
	}

	@Test
	void testLevelDebug() {
		Logger logger = getLogger();
		logger.debug("");
		String buffer = getBuffer();
		assertTrue(buffer.startsWith("[DEBUG]"), buffer);
	}

	@Test
	void testLevelTrace() {
		Logger logger = getLogger();
		logger.trace("");
		String buffer = getBuffer();
		assertTrue(buffer.startsWith("[TRACE]"), buffer);
	}

	@Test
	void testSuppressed() {
		// Uses the default level, INFO
		Logger logger = Logger.create(LoggerTest.class);
		logger.debug("debug message");
		logger.trace("trace message");
		String buffer = getBuffer();
		assertTrue(buffer.isEmpty(), buffer);
	}

	@Test
	void testLoggerMessageOnly() {
		String message = "Test message only";

		Logger logger = getLogger();
		logger.error(message);

		String buffer = getBuffer();
		assertTrue(buffer.contains(message), buffer);
	}

	@Test
	void testLoggerBracesWithNoData() {
		String message = "Test message only {}";

		Logger logger = getLogger();
		logger.error(message);

		String buffer = getBuffer();
		assertTrue(buffer.contains(message), buffer);
	}

	@Test
	void testLoggerBracesWithData() {
		String message = "Test message only {}";

		Logger logger = getLogger();
		logger.error(message, 1);

		String buffer = getBuffer();
		String expected = message.replace("{}", "1");
		assertTrue(buffer.contains(expected), buffer);
	}

	@Test
	void testLoggerNoBracesWithData() {
		String message = "Test message only";

		Logger logger = getLogger();
		logger.error(message, 1);

		String buffer = getBuffer();
		assertTrue(buffer.contains(message + '1'), buffer);
	}

	@NonNull
	private static Logger getLogger() {
		return new Logger(LoggerTest.class, Logger.Level.TRACE);
	}

	@NonNull
	private static
	String getBuffer() {
		return stringBuilder.toString();
	}
}
