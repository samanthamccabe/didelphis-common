package org.didelphis.language.phonetic.features;

import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleFeatureTest {
	
	private static final DoubleFeature FEATURE = DoubleFeature.INSTANCE;
	private static final double NaN = Double.NaN;

	@Test
	void testEmptyLoader() {
		FeatureModelLoader<Double> loader = DoubleFeature.emptyLoader();
		assertEquals(0, loader.getSpecification().size());
	}

	@Test
	void testParseValue() {
		assertEquals( 1.0, (double) FEATURE.parseValue("+"));
		assertEquals( 1.0, (double) FEATURE.parseValue("1"));
		assertEquals(-1.0, (double) FEATURE.parseValue("-"));
		assertEquals(-1.0, (double) FEATURE.parseValue("-1"));
		assertEquals( NaN, (double) FEATURE.parseValue(""));
		assertEquals( 0.0, (double) FEATURE.parseValue("0"));

		assertThrows(NumberFormatException.class, () -> FEATURE.parseValue("/"));
	}

	@Test
	void listUndefined() {
		Collection<Double> actual = FEATURE.listUndefined();
		assertTrue(actual.contains(null));
		assertTrue(actual.contains(Double.NEGATIVE_INFINITY));
		assertTrue(actual.contains(Double.POSITIVE_INFINITY));
		assertTrue(actual.contains(Double.NaN));
	}

	@Test
	void compare() {
		assertEquals(-1, FEATURE.compare(0.0, 1.0));
		assertEquals( 1, FEATURE.compare(1.0, 0.0));
		assertEquals( 0, FEATURE.compare(1.0, 1.0));

		assertEquals( 0, FEATURE.compare(null, 0.0));
		assertEquals( 0, FEATURE.compare(0.0, null));
		assertEquals( 0, FEATURE.compare(null, null));
	}

	@Test
	void difference() {
		assertEquals(0.0, FEATURE.difference(0.0, 0.0));
		assertEquals(0.0, FEATURE.difference(1.0, 1.0));
		assertEquals(1.0, FEATURE.difference(0.0, 1.0));
		assertEquals(1.0, FEATURE.difference(1.0, 0.0));

		assertEquals(0.0, FEATURE.difference(null, (Double) null));
		assertEquals(1.0, FEATURE.difference(1.0, null));
		assertEquals(1.0, FEATURE.difference(null, 1.0));
		assertEquals(0.0, FEATURE.difference(null, 0.0));
	}

	@Test
	void intValue() {
		assertEquals(0, FEATURE.intValue(null));
		assertEquals(0, FEATURE.intValue(0.0));
		assertEquals(1, FEATURE.intValue(1.0));
	}

	@Test
	void doubleValue() {
		assertEquals(NaN, FEATURE.doubleValue(null));
		assertEquals(0.0, FEATURE.doubleValue(0.0));
		assertEquals(1.0, FEATURE.doubleValue(1.0));
	}
}