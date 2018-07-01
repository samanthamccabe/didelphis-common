/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.io;

import org.didelphis.utilities.Logger;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 3/16/17.
 */
class DiskFileHandlerTest {

	private static final Logger LOG = Logger.create(DiskFileHandlerTest.class);

	private final DiskFileHandler handler = new DiskFileHandler("UTF-8");
	
	@Test
	void read() {
		String filePath = "./testFileRead.txt";
		File file = new File(filePath);

		String payload = "Test payload for reading";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
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
	void writeString() {
		String filePath = "./testFileWrite.txt";
		File file = new File(filePath);
		String payload = "Test payload for writing";

		handler.writeString(filePath, payload);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String collect = reader.lines().collect(Collectors.joining("\n"));
			assertEquals(payload, collect);
		} catch (FileNotFoundException e) {
			LOG.error("Failed to read from file {}, file not found", filePath, e);
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
		assertFalse(handler.writeString(filePath, payload));
	}


	@Test
	void testEquals() {
		assertEquals(handler, new DiskFileHandler("UTF-8"));
		assertNotEquals(handler, new DiskFileHandler("ISO-8869-1"));
	}

	@Test
	void test_hashCode() {
		assertEquals(handler.hashCode(), new DiskFileHandler("UTF-8").hashCode());
		assertNotEquals(handler.hashCode(), new DiskFileHandler("UTF-16").hashCode());
	}

	@Test
	void test_equals() {
		assertEquals(handler, new DiskFileHandler("UTF-8"));
		assertNotEquals(handler, new DiskFileHandler("UTF-16"));
	}

	@Test
	void test_toString() {
		assertEquals(handler.toString(), new DiskFileHandler("UTF-8").toString());
		assertNotEquals(handler.toString(), new DiskFileHandler("UTF-16").toString());
	}
}
