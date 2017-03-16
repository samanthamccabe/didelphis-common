package org.didelphis.common.language.phonetic.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by samantha on 2/19/17.
 */
class FeatureTypeTest {
	@Test
	void findBinary() {
		assertEquals(FeatureType.BINARY, FeatureType.find("BINARY"));
		assertEquals(FeatureType.BINARY, FeatureType.find("binary"));
	}

	@Test
	void matchesBinary() {
		FeatureType type = FeatureType.BINARY;
		assertTrue(type.matches("+"));
		assertTrue(type.matches("-"));
		assertTrue(type.matches("\u2212"));
		
		assertFalse(type.matches("0"));
		assertFalse(type.matches("1"));
		assertFalse(type.matches("a"));
	}

	@Test
	void findTernary() {
		assertEquals(FeatureType.TERNARY, FeatureType.find("TERNARY"));
		assertEquals(FeatureType.TERNARY, FeatureType.find("ternary"));
	}

	@Test
	void matchesTernary() {
		FeatureType type = FeatureType.TERNARY;
		assertTrue(type.matches("+"));
		assertTrue(type.matches("-"));
		assertTrue(type.matches("\u2212"));
		assertTrue(type.matches("0"));
		
		assertFalse(type.matches("1"));
		assertFalse(type.matches("a"));
	}

	@Test
	void findNumeric() {
		assertEquals(FeatureType.NUMERIC, FeatureType.find("NUMERIC"));
		assertEquals(FeatureType.NUMERIC, FeatureType.find("numeric"));
	}

	@Test
	void matchesNumeric() {
		FeatureType type = FeatureType.NUMERIC;
		assertTrue(type.matches("1"));
		assertTrue(type.matches("0"));

		assertTrue(type.matches("0.5"));
		assertTrue(type.matches("22"));
		assertTrue(type.matches("44.33"));
		assertTrue(type.matches("1.002"));

		assertTrue(type.matches("-2"));
		assertTrue(type.matches("\u22122"));

		assertFalse(type.matches("2."));
		assertFalse(type.matches("+4"));
		assertFalse(type.matches("+"));
		assertFalse(type.matches("-"));
		assertFalse(type.matches("\u2212"));
		assertFalse(type.matches("a"));
	}
}
