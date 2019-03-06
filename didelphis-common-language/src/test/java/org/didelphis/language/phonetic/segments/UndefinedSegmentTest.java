/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

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