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

package org.didelphis.io;

import org.didelphis.utilities.Logger;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DiskFileHandlerTest {

	private static final Logger LOG = Logger.create(DiskFileHandlerTest.class);

	private final DiskFileHandler handler = new DiskFileHandler("UTF-8");

	@Test
	void read() throws IOException {
		String filePath = "./testFileRead.txt";
		File file = new File(filePath);

		String payload = "Test payload for reading";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(payload);
		} catch (IOException e) {
			LOG.error("Failed to create file {}", filePath, e);
		}

		CharSequence sequence = handler.read(filePath);
		assertEquals(payload, sequence.toString());

		// If deleting the file immediately fails, attempt to delete it on
		// JVM shutdown, when the tests conclude
		if (!file.delete()) {
			file.deleteOnExit();
		}
	}

	@Test
	void writeString() throws IOException {
		String path = "./testFileWrite.txt";
		File file = new File(path);
		String payload = "Test payload for writing";

		handler.writeString(path, payload);

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String collect = reader.lines().collect(Collectors.joining("\n"));
			assertEquals(payload, collect);
		} catch (FileNotFoundException e) {
			LOG.error("Failed to read from file {}, file not found", path, e);
		} catch (IOException e) {
			LOG.error("Failed to read from file {}", e);
		}

		// If deleting the file immediately fails, attempt to delete it on
		// JVM shutdown, when the tests conclude
		if (!file.delete()) {
			file.deleteOnExit();
		}
	}

	@Test
	void writeString_Fail() {
		String filePath = "./foo/testFileWrite.txt";
		String payload = "Expected Failure";
		assertThrows(
				IOException.class,
				() -> handler.writeString(filePath, payload)
		);
	}

	@Test
	void testEquals() {
		assertEquals(new DiskFileHandler("UTF-8"), handler);
		assertNotEquals(new DiskFileHandler("ISO-8869-1"), handler);
	}

	@Test
	void test_hashCode() {
		assertEquals(new DiskFileHandler("UTF-8").hashCode(), handler.hashCode());
		assertNotEquals(new DiskFileHandler("UTF-16").hashCode(), handler.hashCode());
	}

	@Test
	void test_equals() {
		assertEquals(new DiskFileHandler("UTF-8"), handler);
		assertNotEquals(new DiskFileHandler("UTF-16"), handler);
	}

	@Test
	void test_toString() {
		assertEquals(handler.toString(), new DiskFileHandler("UTF-8").toString());
		assertNotEquals(handler.toString(), new DiskFileHandler("UTF-16").toString());
	}

	@Test
	void testValidForRead() {
		String path1 = "didelphis-common-io/src/test/resources/testFile.txt";
		String path2 = "didelphis-common-io/src/test/resources/testFile_doesntExist.txt";

		assertTrue(handler.validForRead(path1));
		assertFalse(handler.validForRead(path2));

		assertThrows(
				NullPointerException.class,
				() -> handler.validForRead(null)
		);
	}

	@Test
	void testValidForWrite() {
		String path1 = "didelphis-common-io/src/test/resources/testFile.txt";
		String path2 = "didelphis-common-io/src/test/resources/testFile_doesntExist.txt";

		assertTrue(handler.validForWrite(path1));
		assertFalse(handler.validForWrite(path2));

		assertThrows(
				NullPointerException.class,
				() -> handler.validForWrite(null)
		);
	}
}
