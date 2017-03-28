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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by samantha on 3/16/17.
 */
class DiskFileHandlerTest {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(DiskFileHandlerTest.class);

	private final DiskFileHandler handler = new DiskFileHandler("UTF-8");
	
	@Test
	void read() {
		String filePath = "./testFileRead.txt";
		File file = new File(filePath);

		String payload = "Test payload for reading";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
			writer.write(payload);
		} catch (IOException e) {
			LOGGER.error("Failed to create file {}", filePath, e);
		}

		CharSequence sequence = handler.read(filePath);
		
		assertThat(payload, is(sequence.toString()));
		
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
			assertThat(collect, is(payload));
		} catch (FileNotFoundException e) {
			LOGGER.error("Failed to read from file {}, file not found", filePath, e);
		} catch (IOException e) {
			LOGGER.error("Failed to read from file {}", e);
		}

		// If deleting the file immediately fails, attempt to delete it on
		// JVM shutdown, when the tests conclude
		if (!file.delete()) {
			file.deleteOnExit();
		}
	}
	
	@Test
	void testEquals() {
		assertThat(new DiskFileHandler("UTF-8"), is(handler));
		assertThat(new DiskFileHandler("ISO-8869-1"), not(handler));
	}

}
