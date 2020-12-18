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

package org.didelphis.language.phonetic.features;

import org.didelphis.language.phonetic.PhoneticTestBase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmptyFeatureArrayTest extends PhoneticTestBase {

	private final FeatureArray array = new EmptyFeatureArray(factory.getFeatureMapping().getFeatureModel());

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
		FeatureArray f1 = factory.toSegment("x").getFeatures();
		FeatureArray f2 = factory.toSegment("z").getFeatures();
		FeatureArray f3 = new EmptyFeatureArray(array);

		assertNotEquals(array.toString(), f1.toString());
		assertNotEquals(array.toString(), f2.toString());
		assertEquals(array.toString(), f3.toString());
		assertEquals(array.toString(), array.toString());
	}

	@Test
	void testMatches() {
		FeatureArray features = factory.toSegment("a").getFeatures();
		assertFalse(array.matches(features));
		assertTrue(array.matches(array));
	}

	@Test
	void testAlter() {
		FeatureArray features = factory.toSegment("a").getFeatures();
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
		FeatureArray f1 = factory.toSegment("x").getFeatures();
		FeatureArray f2 = factory.toSegment("z").getFeatures();
		FeatureArray f3 = new EmptyFeatureArray(array);

		assertEquals(-1, array.compareTo(f1));
		assertEquals(-1, array.compareTo(f2));
		assertEquals(0, array.compareTo(f3));

		assertEquals(array.compareTo(f1), -1 * f1.compareTo(array));
		assertEquals(array.compareTo(f2), -1 * f2.compareTo(array));
	}
}
