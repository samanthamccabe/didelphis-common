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
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.segments.Segment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardFeatureArrayTest extends PhoneticTestBase {

	private static final Integer NULL = null;
	private static FeatureModel empty;
	private static FeatureModel model;

	private StandardFeatureArray array;

	@BeforeAll
	static void initModel() {
		model = loader.getFeatureModel();
		empty = IntegerFeature.INSTANCE.emptyLoader().getFeatureModel();
	}

	@BeforeEach
	void initArray() {
		array = new StandardFeatureArray(1, model);
	}

	@Test
	void testConstructor() {

		Segment segment = factory.toSegment("a");

		FeatureArray features = segment.getFeatures();

		List<Integer> list = new ArrayList<>();
		for (Integer integer : features) {
			list.add(integer);
		}

		FeatureArray array = new StandardFeatureArray(
				list,
				features.getFeatureModel()
		);
		assertEquals(features, array);
	}

	@Test
	void size() {
		assertEquals(20, array.size());
	}

	@Test
	void set() {
		array.set(0, -1);
		array.set(4, -2);

		assertEquals(-1, (int) array.get(0));
		assertEquals(-2, (int) array.get(4));
	}

	@Test
	void get() {
		assertEquals(1, (int) array.get(0));
		assertEquals(1, (int) array.get(1));
	}

	@Test
	void matches() {
		assertTrue(array.matches(new StandardFeatureArray(NULL, model)));
	}

	@Test
	void alter() {
		FeatureArray mask = new StandardFeatureArray(NULL, model);
		mask.set(10, 9);
		array.alter(mask);

		assertEquals(9, (int) array.get(10));
		assertEquals(1, (int) array.get(5));
	}

	@Test
	void alterException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> array.alter(new StandardFeatureArray(NULL, empty))
		);
	}

	@Test
	void matchesException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> array.matches(new StandardFeatureArray(NULL, empty))
		);
	}

	@Test
	void compareToException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> array.compareTo(new StandardFeatureArray(0, empty))
		);
	}

	@Test
	void contains() {
		assertFalse(array.contains(-1));
		assertTrue(array.contains(1));
	}

	@Test
	void compareTo() {
		FeatureArray array1 = new StandardFeatureArray(array);
		FeatureArray array2 = new StandardFeatureArray(array);
		FeatureArray array3 = new StandardFeatureArray(array);

		array1.set(0, -1);
		array2.set(0, 3);

		assertEquals(1, array.compareTo(array1));
		assertEquals(0, array.compareTo(array3));
		assertEquals(-1, array.compareTo(array2));
	}

	@Test
	void compareToNulls() {
		FeatureArray array1 = new StandardFeatureArray(0, model);
		FeatureArray array2 = new StandardFeatureArray(NULL, model);
		FeatureArray array3 = new StandardFeatureArray(array);

		array1.set(3, NULL);
		array1.set(5, NULL);
		array1.set(7, NULL);

		assertEquals(1, array.compareTo(array1));
		assertEquals(1, array.compareTo(array2));
		assertEquals(1, array1.compareTo(array2));
		assertEquals(-1, array2.compareTo(array1));
		assertEquals(0, array.compareTo(array3));
		assertEquals(0, array2.compareTo(new StandardFeatureArray(NULL, model)));
	}

	@Test
	void equals() {
		assertEquals(array, new StandardFeatureArray(1, model));
		assertNotEquals(array, new StandardFeatureArray(-1, model));
	}

	@Test
	void iterator() {
		assertTrue(array.iterator().hasNext());
	}

	@Test
	void getFeatureModel() {
		assertEquals(model, array.getFeatureModel());
	}

}
