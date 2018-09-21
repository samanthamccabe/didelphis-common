package org.didelphis.language.phonetic.features;

import org.didelphis.language.phonetic.PhoneticTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmptyFeatureArrayTest extends PhoneticTestBase {

	private final FeatureArray<Integer> array = new EmptyFeatureArray<>(factory.getFeatureMapping().getFeatureModel());
	
	@Test
	void testSet() {
		assertThrows(
				UnsupportedOperationException.class,
				() -> array.set(0, 0)
		);
	}
	
	@Test
	void testIterator() {
		assertFalse(array.iterator().hasNext());
	}

	@Test
	void testGet() {
		assertNull(array.get(0));
		assertNull(array.get(10));
		
		assertThrows(IndexOutOfBoundsException.class, () -> array.get(22));
	}

	@Test
	void testToString() {
		FeatureArray<Integer> f1 = factory.toSegment("x").getFeatures();
		FeatureArray<Integer> f2 = factory.toSegment("z").getFeatures();
		FeatureArray<Integer> f3 = new EmptyFeatureArray<>(array);
		
		assertNotEquals(array.toString(), f1.toString());
		assertNotEquals(array.toString(), f2.toString());
		assertEquals(array.toString(), f3.toString());
		assertEquals(array.toString(), array.toString());
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
		FeatureArray<Integer> f1 = factory.toSegment("x").getFeatures();
		FeatureArray<Integer> f2 = factory.toSegment("z").getFeatures();
		FeatureArray<Integer> f3 = new EmptyFeatureArray<>(array);

		assertEquals(-1, array.compareTo(f1));
		assertEquals(-1, array.compareTo(f2));
		assertEquals(0, array.compareTo(f3));
		
		assertEquals(array.compareTo(f1), -1 * f1.compareTo(array));
		assertEquals(array.compareTo(f2), -1 * f2.compareTo(array));
	}
}