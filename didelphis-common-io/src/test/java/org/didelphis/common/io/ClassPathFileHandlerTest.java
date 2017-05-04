package org.didelphis.common.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by samantha on 3/16/17.
 */
class ClassPathFileHandlerTest {
	@Test
	void read() {
		String received = ClassPathFileHandler.INSTANCE.read("testFile.txt");
		String expected = "this is a test file for ClassPathFileHandlerTest\n";
		assertEquals(expected, received);
	}
	
	@Test
	void write() {
		assertThrows(UnsupportedOperationException.class, () -> ClassPathFileHandler.INSTANCE.writeString("",""));
	}
}
