package org.didelphis.common.language.enums;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by samantha on 4/15/17.
 */
@Disabled // TODO: 
class FormatterModeTest {
	@Test
	void normalizeNone() {
		String string = "string";
		assertEquals(string, FormatterMode.NONE.normalize(string));
	}

	@Test
	void splitNone() {
		String string = "string";
		assertTrue(false);
	}

	@Test
	void normalizeDecomposition() {
		assertTrue(false);
	}

	@Test
	void splitDecomposition() {
		assertTrue(false);
	}

	@Test
	void normalizeComposition() {
		assertTrue(false);
	}

	@Test
	void splitComposition() {
		assertTrue(false);
	}

	@Test
	void normalizeIntelligent() {
		assertTrue(false);
	}

	@Test
	void splitIntelligent() {
		assertTrue(false);
	}
	
	@Test
	void valueOf() {
	}

}
