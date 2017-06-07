package org.didelphis.common.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class NullFileHandlerTest
 *
 * @since 06/06/2017
 */
class NullFileHandlerTest {
	@Test
	void read() {
		assertNull(NullFileHandler.INSTANCE.read(""));
	}

	@Test
	void writeString() {
		assertFalse(NullFileHandler.INSTANCE.writeString("", ""));
	}

}