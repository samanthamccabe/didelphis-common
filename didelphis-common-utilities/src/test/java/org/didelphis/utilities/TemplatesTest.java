package org.didelphis.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplatesTest {

	@Test
	void create() {
		String message = Templates.create()
				.add("Too few features are provided!")
				.add("Found {} but was expecting {}")
				.with(2, 3)
				.data("w\t+\t+")
				.build();
		
		String expected = "Too few features are provided! " +
				"Found 2 but was expecting 3\nWith Data:\n\tw\t+\t+" +
				"\n\n";
		
		assertEquals(expected, message);
	}

	@Test
	void compile() {
		String expected = "Found 2 but was expecting 3";
		String template = "Found {} but was expecting {}";
		String actual = Templates.compile(template, 2, 3);
		assertEquals(expected, actual);
	}
}