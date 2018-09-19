package org.didelphis.language.phonetic.segments;

import org.didelphis.language.phonetic.PhoneticTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UndefinedSegmentTest extends PhoneticTestBase {

	private final Segment<Integer> segment = getSegment("segment");

	@Test
	void testAlter() {
		Segment<Integer> mod = factory.toSegment("[-voice]");
		assertFalse(segment.alter(mod));
		assertEquals(segment, segment);
	}

	@Test
	void testGetSymbol() {
		assertEquals("segment", segment.getSymbol());
	}

	@Test
	void testGetFeatures() {
		int size = factory.getFeatureMapping()
				.getFeatureModel()
				.getSpecification()
				.size();
		
		assertEquals(size, segment.getFeatures().size());
	}

	@Test
	void testIsDefinedInModel() {
		assertFalse(segment.isDefinedInModel());
	}

	@Test
	void testToString() {
		assertEquals("segment", segment.toString());
	}

	@Test
	void getFeatureModel() {
		assertEquals(factory.getFeatureMapping().getFeatureModel(), segment.getFeatureModel());
	}

	@Test
	void testEquals() {
		assertEquals(segment, segment);
		assertNotEquals(getSegment("x"), segment);
	}

	@Test
	void testHashCode() {
		assertEquals(segment.hashCode(), segment.hashCode());
		assertNotEquals(getSegment("x").hashCode(), segment.hashCode());
	}
	
	private static Segment<Integer> getSegment(String symbol) {
		return new UndefinedSegment<>(
				symbol,
				factory.getFeatureMapping().getFeatureModel()
		);
	}
}