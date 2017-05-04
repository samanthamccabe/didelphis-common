package org.didelphis.common.language.phonetic.features;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.language.phonetic.model.empty.EmptyFeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by samantha on 4/15/17.
 */
class SparseFeatureArrayTest {
	
	private static FeatureModel<Double> model;
	private SparseFeatureArray<Double> array;

	@BeforeAll
	static void initModel() {
		model = FeatureModelLoader.loadDouble(
				"AT_hybrid.model",
				ClassPathFileHandler.INSTANCE
		);
	}

	@BeforeEach
	void initArray() {
		array = new SparseFeatureArray<>(model);
	}

	@Test
	void testListConstructorEmpty() {
		SparseFeatureArray<Double> empty = new SparseFeatureArray<>(new ArrayList<>(), model);
		
		assertEquals(array, empty);
	}

	@Test
	void testListConstructorNonEmpty() {
		ArrayList<Double> list = new ArrayList<>(20);
		Collections.addAll(list, 
				null,
				2.0,
				null,
				4.0,
				null,
				null,
				null,
				8.0
		);
		
		array.set(1, 2.0);
		array.set(3, 4.0);
		array.set(7, 8.0);
		
		SparseFeatureArray<Double> array1 = new SparseFeatureArray<>(list, model);

		assertEquals(array, array1);
	}
	
	@Test
	void size() {
		assertEquals(20, array.size());
	}

	@Test
	void set() {
		array.set(0, 1.0);
		assertNotNull(array.get(0));
	}

	@Test
	void get() {
		assertNull(array.get(0));
	}

	@Test
	void getOutOfBounds() {
		assertThrows(
				IndexOutOfBoundsException.class,
				() -> array.get(21));
	}

	@Test
	void setOutOfBounds() {
		assertThrows(
				IndexOutOfBoundsException.class, 
				() -> array.set(21, 0.0));
	}
	
	@Test
	void matches() {
		FeatureArray<Double> array1 = new StandardFeatureArray<>(1.0, model);
		array.set(2, 1.0);
		array.set(4, 1.0);
		
		assertTrue(array.matches(array1));
	}

	@Test
	void alter() {
		FeatureArray<Double> array1 = new StandardFeatureArray<>(1.0, model);
		array.set(2, 1.0);
		array.set(4, 1.0);

		array.alter(array1);
		
		assertEquals(1.0, (double) array.get(2));
		assertEquals(1.0, (double) array.get(4));
	}

	@Test
	void alterIllegalArgument() {
		assertThrows(
				IllegalArgumentException.class, 
				() -> array.alter(new SparseFeatureArray<>(EmptyFeatureModel.DOUBLE)));
	}

	@Test
	void matchesIllegalArgument() {
		assertThrows(
				IllegalArgumentException.class,
				() -> array.matches(new SparseFeatureArray<>(EmptyFeatureModel.DOUBLE)));
	}


	@Test
	void compareIllegalArgument() {
		assertThrows(
				IllegalArgumentException.class,
				() -> array.compareTo(new SparseFeatureArray<>(EmptyFeatureModel.DOUBLE)));
	}

	@Test
	void contains() {
		assertFalse(array.contains(1.0));
		assertFalse(array.contains(-1.0));
	}

	@Test
	void containsWithValues() {
		
		array.set(4, 1.0);
		
		assertTrue(array.contains(1.0));
		assertFalse(array.contains(-1.0));
	}

	@Test
	void compareTo() {
		SparseFeatureArray<Double> array1 = new SparseFeatureArray<>(array);
		SparseFeatureArray<Double> array2 = new SparseFeatureArray<>(array);
		SparseFeatureArray<Double> array3 = new SparseFeatureArray<>(array);
		
		array1.set(0, 0.0);
		array2.set(0, 0.0);
		array2.set(1, 1.0);
		array3.set(0, 0.0);
		array3.set(1, 1.0);
		array3.set(2, 0.0);
		array3.set(3, 2.0);
		
		assertEquals(-1, array.compareTo(array1));
		assertEquals(-1, array.compareTo(array2));
		assertEquals(-1, array.compareTo(array3));
		assertEquals(1, array1.compareTo(array));
		assertEquals(1, array2.compareTo(array));
		assertEquals(1, array3.compareTo(array));

		assertEquals(-1, array1.compareTo(array2));
		assertEquals(-1, array1.compareTo(array3));
		
		assertEquals(1, array2.compareTo(array1));
		assertEquals(-1, array2.compareTo(array3));
		
		assertEquals(1, array3.compareTo(array2));
		assertEquals(1, array3.compareTo(array1));

		SparseFeatureArray<Double> array4 = new SparseFeatureArray<>(array3);
		assertEquals(0, array3.compareTo(array4));
		assertEquals(0, array4.compareTo(array3));
	}

	@Test
	void iterator() {
		List<Double> valuesReceived = new ArrayList<>(20);
		List<Double> valuesExpected = new ArrayList<>(20);
		array.iterator().forEachRemaining(valuesReceived::add);
		Collections.fill(valuesExpected, null);
		assertEquals(valuesExpected, valuesReceived);
	}

	@Test
	void equals() {
		SparseFeatureArray<Double> array1 = new SparseFeatureArray<>(array);
		SparseFeatureArray<Double> array2 = new SparseFeatureArray<>(array);
	
		array2.set(4, 1.0);
		
		assertEquals(array, array1);
		assertNotEquals(array, array2);
	}

	@Test
	void getFeatureModel() {
		assertEquals(model, array.getFeatureModel());
	}

	@Test
	void getSpecification() {
		assertEquals(model,array.getSpecification());
	}
	
	@Test
	void testHashCode() {
		assertEquals(array.hashCode(), new SparseFeatureArray<>(array).hashCode());
		SparseFeatureArray<Double> array1 = new SparseFeatureArray<>(array);
		array1.set(0, 0.0);
		assertNotEquals(array.hashCode(), array1.hashCode());
		assertNotEquals(array.hashCode(), new SparseFeatureArray<>(EmptyFeatureModel.DOUBLE));
	}

}
