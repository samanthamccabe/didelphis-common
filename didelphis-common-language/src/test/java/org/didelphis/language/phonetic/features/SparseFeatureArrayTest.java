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

import org.didelphis.io.ClassPathFileHandler;
import org.didelphis.language.phonetic.PhoneticTestBase;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureModelLoader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class SparseFeatureArrayTest extends PhoneticTestBase {

	private static FeatureModel<Integer> empty;
	private static FeatureModel<Integer> model;
	private SparseFeatureArray<Integer> array;

	@BeforeAll
	static void initModel() {
		model = loader.getFeatureModel();
		empty = new FeatureModelLoader<>(
				IntegerFeature.INSTANCE,
				ClassPathFileHandler.INSTANCE,
				Collections.emptyList(), ""
		).getFeatureModel();
	}

	@BeforeEach
	void initArray() {
		array = new SparseFeatureArray<>(model);
	}

	@Test
	void testListConstructorEmpty() {
		List<Integer> array = new ArrayList<>();
		FeatureArray<Integer> empty = new SparseFeatureArray<>(array, model);
		assertEquals(this.array, empty);
	}

	@Test
	void testListConstructorNonEmpty() {
		array.set(1, 2);
		array.set(3, 4);
		array.set(6, 8);
		List<Integer> list = Arrays.asList(null, 2, null, 4, null, null, 8);
		FeatureArray<Integer> array1 = new SparseFeatureArray<>(list, model);
		assertEquals(array, array1);
	}

	@Test
	void size() {
		assertEquals(20, array.size());
	}

	@Test
	void set() {
		array.set(0, 1);
		assertNotNull(array.get(0));
	}

	@Test
	void get() {
		assertNull(array.get(0));
	}

	@Test
	void getOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class, () -> array.get(21));
	}

	@Test
	void setOutOfBounds() {
		assertThrows(IndexOutOfBoundsException.class, () -> array.set(21, 0));
	}

	@Test
	void matches() {
		FeatureArray<Integer> array1 = new StandardFeatureArray<>(1, model);
		array.set(2, 1);
		array.set(4, 1);

		assertTrue(array.matches(array1));
	}

	@Test
	void alter() {
		FeatureArray<Integer> array1 = new StandardFeatureArray<>(1, model);
		array.set(2, 1);
		array.set(4, 1);

		array.alter(array1);

		assertEquals(1, (int) array.get(2));
		assertEquals(1, (int) array.get(4));
	}

	@Test
	void alterIllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> array.alter(new SparseFeatureArray<>(empty)));
	}

	@Test
	void matchesIllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> array.matches(new SparseFeatureArray<>(empty)));
	}

	@Test
	void compareIllegalArgument() {
		assertThrows(IllegalArgumentException.class,
				() -> array.compareTo(new SparseFeatureArray<>(empty)));
	}

	@Test
	void contains() {
		assertFalse(array.contains(1));
		assertFalse(array.contains(-1));
	}

	@Test
	void containsWithValues() {

		array.set(4, 1);

		assertTrue(array.contains(1));
		assertFalse(array.contains(-1));
	}

	@Test
	void compareTo() {
		FeatureArray<Integer> array1 = new SparseFeatureArray<>(array);
		FeatureArray<Integer> array2 = new SparseFeatureArray<>(array);
		FeatureArray<Integer> array3 = new SparseFeatureArray<>(array);

		array1.set(0, 0);
		array2.set(0, 0);
		array2.set(1, 1);
		array3.set(0, 0);
		array3.set(1, 1);
		array3.set(2, 0);
		array3.set(3, 2);

		assertEquals( 0, array.compareTo(array1));
		assertEquals(-1, array.compareTo(array2));
		assertEquals(-1, array.compareTo(array3));
		assertEquals( 0, array1.compareTo(array));
		assertEquals( 1, array2.compareTo(array));
		assertEquals( 1, array3.compareTo(array));

		assertEquals(-1, array1.compareTo(array2));
		assertEquals(-1, array1.compareTo(array3));

		assertEquals( 1, array2.compareTo(array1));
		assertEquals(-1, array2.compareTo(array3));

		assertEquals( 1, array3.compareTo(array2));
		assertEquals( 1, array3.compareTo(array1));

		FeatureArray<Integer> array4 = new SparseFeatureArray<>(array3);
		assertEquals(0, array3.compareTo(array4));
		assertEquals(0, array4.compareTo(array3));
	}

	@Test
	void iterator() {
		List<Integer> valuesReceived = new ArrayList<>(20);
		List<Integer> valuesExpected = new ArrayList<>(20);
		array.iterator().forEachRemaining(valuesReceived::add);
		Collections.fill(valuesExpected, null);
		assertEquals(valuesExpected, valuesReceived);
	}

	@Test
	void equals() {
		FeatureArray<Integer> array1 = new SparseFeatureArray<>(array);
		FeatureArray<Integer> array2 = new SparseFeatureArray<>(array);

		array2.set(4, 1);

		assertEquals(array, array1);
		assertNotEquals(array, array2);
	}

	@Test
	void getFeatureModel() {
		assertEquals(model, array.getFeatureModel());
	}

	@Test
	void getSpecification() {
		assertEquals(model.getSpecification(),array.getSpecification());
	}

	@Test
	void testHashCode() {
		assertEquals(array.hashCode(), new SparseFeatureArray<>(array).hashCode());
		assertNotEquals(array.hashCode(), new SparseFeatureArray<>(empty));
		FeatureArray<Integer> array1 = new SparseFeatureArray<>(array);
		array1.set(0, 0);
		assertNotEquals(array.hashCode(), array1.hashCode());
	}

}
