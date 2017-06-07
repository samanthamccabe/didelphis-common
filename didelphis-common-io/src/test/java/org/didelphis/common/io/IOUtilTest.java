package org.didelphis.common.io;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class IOUtilTest
 *
 * @since 06/05/2017
 */
class IOUtilTest {

	@Test
	void readPath() {
		String data = IOUtil.readPath("didelphis-common-io/src/main/resources/testFile.txt");
		assertNotNull(data);
		assertFalse(data.isEmpty());
	}

	@Test
	void readPath_Fail() {
		String data = IOUtil.readPath("willfail");
		assertNull(data);
	}

	@Test
	void readStream() {
		String name = "testFile.txt";
		InputStream resource = IOUtilTest.class.getClassLoader().getResourceAsStream(name);
		String data = IOUtil.readStream(resource);
		assertNotNull(data);
		assertFalse(data.isEmpty());
	}
}