package org.didelphis.language.phonetic.features;

import org.didelphis.language.phonetic.PhoneticTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmptyFeatureArrayTest extends PhoneticTestBase {

	private final FeatureArray<Integer> array = new EmptyFeatureArray<>(factory.getFeatureMapping().getFeatureModel());
	
	@Test
	void testSize() {
	}

	@Test
	void testSet() {
		assertThrows(
				UnsupportedOperationException.class,
				() -> array.set(0, 0)
		);
	}

	@Test
	void testGet() {
		assertNull(array.get(0));
		assertNull(array.get(10));
	}

	@Test
	void testMatches() {
		FeatureArray<Integer> features = factory.toSegment("a").getFeatures();
		assertFalse(array.matches(features));
		assertTrue(array.matches(array));
	}

	@Test
	void testAlter() {
		FeatureArray<Integer> features = factory.toSegment("a").getFeatures();
		assertThrows(
				UnsupportedOperationException.class,
				() -> array.alter(features)
		);
	}

	@Test
	void testContains() {
		assertFalse(array.contains(null));
		assertFalse(array.contains(-1));
		assertFalse(array.contains(0));
		assertFalse(array.contains(1));
		assertFalse(array.contains(2));
	}

	@Test
	void testCompareTo() {
	}

	@Test
	void testGetFeatureModel() {
		
	}
	
	@Test
	void testIterator() {
		
	}

	@Test
	void testEquals() {
	}

	@Test
	void testHashCode() {
	}

	@Test
	void testToString() {
	}
}