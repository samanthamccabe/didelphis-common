/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardFeatureArrayTest extends PhoneticTestBase {

	private static final Integer NULL = null;
	private static FeatureModel<Integer> empty;
	private static FeatureModel<Integer> model;
	
	private StandardFeatureArray<Integer> array;
	
	@BeforeAll
	static void initModel() {
		model = loader.getFeatureModel();
		empty = IntegerFeature.emptyLoader().getFeatureModel();
	}
	
	@BeforeEach
	void initArray() {
		array = new StandardFeatureArray<>(1, model);
	}
	
	@Test
	void testConstructor() {

		Segment<Integer> segment = factory.toSegment("a");

		FeatureArray<Integer> features = segment.getFeatures();

		List<Integer> list = new ArrayList<>();
		for (Integer integer : features) {
			list.add(integer);
		}

		FeatureArray<Integer> array = new StandardFeatureArray<>(
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
		assertTrue(array.matches(new StandardFeatureArray<>(NULL, model)));
	}

	@Test
	void alter() {
		FeatureArray<Integer> mask = new StandardFeatureArray<>(NULL, model);
		mask.set(10, 9);
		array.alter(mask);
		
		assertEquals(9, (int) array.get(10));
		assertEquals(1, (int) array.get(5));
	}

	@Test
	void alterException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> array.alter(new StandardFeatureArray<>(NULL, empty))
		);
	}

	@Test
	void matchesException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> array.matches(new StandardFeatureArray<>(NULL, empty))
		);
	}

	@Test
	void compareToException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> array.compareTo(new StandardFeatureArray<>(0, empty))
		);
	}
	
	@Test
	void contains() {
		assertFalse(array.contains(-1));
		assertTrue(array.contains(1));
	}

	@Test
	void compareTo() {
		FeatureArray<Integer> array1 = new StandardFeatureArray<>(array);
		FeatureArray<Integer> array2 = new StandardFeatureArray<>(array);
		FeatureArray<Integer> array3 = new StandardFeatureArray<>(array);
		
		array1.set(0, -1);
		array2.set(0, 3);
		
		assertEquals(1, array.compareTo(array1));
		assertEquals(0, array.compareTo(array3));
		assertEquals(-1, array.compareTo(array2));
	}
	
	@Test
	void compareToNulls() {
		FeatureArray<Integer> array1 = new StandardFeatureArray<>(0, model);
		FeatureArray<Integer> array2 = new StandardFeatureArray<>(NULL, model);
		FeatureArray<Integer> array3 = new StandardFeatureArray<>(array);
		
		array1.set(3, NULL);
		array1.set(5, NULL);
		array1.set(7, NULL);
		
		assertEquals(1, array.compareTo(array1));
		assertEquals(1, array.compareTo(array2));
		assertEquals(1, array1.compareTo(array2));
		assertEquals(-1, array2.compareTo(array1));
		assertEquals(0, array.compareTo(array3));
		assertEquals(0, array2.compareTo(new StandardFeatureArray<>(NULL, model)));
	}

	@Test
	void equals() {
		assertEquals(array, new StandardFeatureArray<>(1, model));
		assertNotEquals(array, new StandardFeatureArray<>(-1, model));
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
