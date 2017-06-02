package org.didelphis.common.io;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 3/16/17.
 */
class DiskFileHandlerTest {

	private static final Logger LOG = LoggerFactory.getLogger(DiskFileHandlerTest.class);

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
	void testEquals() {
		assertEquals(handler, new DiskFileHandler("UTF-8"));
		assertNotEquals(handler, new DiskFileHandler("ISO-8869-1"));
	}

}
