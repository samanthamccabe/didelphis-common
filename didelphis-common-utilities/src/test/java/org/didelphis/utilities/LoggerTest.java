/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.utilities;

import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
		Logger logger = Logger.create(LoggerTest.class);
		logger.debug("debug message");
		logger.trace("trace message");
		String buffer = getBuffer();
		assertTrue(buffer.isEmpty(), buffer);
	}

	@Test
	void testSuppressedTrace() {
		Logger logger = Logger.create(LoggerTest.class, Logger.Level.DEBUG);
		logger.trace("trace message");
		logger.debug("debug message");
		String buffer = getBuffer();
		assertFalse(buffer.isEmpty(), buffer);
		assertTrue(buffer.startsWith("[DEBUG]"), buffer);
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
		return Logger.create(LoggerTest.class, Logger.Level.TRACE);
	}

	@NonNull
	private static
	String getBuffer() {
		return stringBuilder.toString();
	}
}
