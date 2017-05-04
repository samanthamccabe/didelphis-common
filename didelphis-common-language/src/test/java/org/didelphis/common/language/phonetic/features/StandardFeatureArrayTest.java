package org.didelphis.common.language.phonetic.features;

import org.didelphis.common.io.ClassPathFileHandler;
import org.didelphis.common.language.phonetic.model.empty.EmptyFeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.loaders.FeatureModelLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by samantha on 4/16/17.
 */
class StandardFeatureArrayTest {

	private static final Double NULL = null;
	private static FeatureModel<Double> model;
	
	private StandardFeatureArray<Double> array;
	
	@BeforeAll
	static void initModel() {
		model = FeatureModelLoader.loadDouble(
				"AT_hybrid.model", 
				ClassPathFileHandler.INSTANCE
		);
	}
	
	@BeforeEach
	void initArray() {
		array = new StandardFeatureArray<>(1.0, model);
	}
	
	@Test
	void size() {
		assertEquals(20, array.size());
	}

	@Test
	void set() {
		array.set(0, -1.0);
		array.set(4, -2.0);
		
		assertEquals(-1.0, (double) array.get(0));
		assertEquals(-2.0, (double) array.get(4));
	}

	@Test
	void get() {
		assertEquals(1.0, (double) array.get(0));
		assertEquals(1.0, (double) array.get(1));
	}

	@Test
	void matches() {
		assertTrue(array.matches(new StandardFeatureArray<>(NULL, model)));
	}

	@Test
	void alter() {
		FeatureArray<Double> mask = new StandardFeatureArray<>(NULL, model);
		mask.set(10, 9.0);
		array.alter(mask);
		
		assertEquals(9.0, (double) array.get(10));
		assertEquals(1.0, (double) array.get(5));
	}

	@Test
	void alterException() {
		assertThrows(IllegalArgumentException.class, () -> array.alter(
				new StandardFeatureArray<>(NULL, EmptyFeatureModel.DOUBLE)));
	}


	@Test
	void matchesException() {
		assertThrows(IllegalArgumentException.class, () -> array.matches(
				new StandardFeatureArray<>(NULL, EmptyFeatureModel.DOUBLE)));
	}
	
	@Test
	void contains() {
		assertFalse(array.contains(-1.0));
		assertTrue(array.contains(1.0));
	}

	@Test
	void compareTo() {
		StandardFeatureArray<Double> array1 = new StandardFeatureArray<>(array);
		StandardFeatureArray<Double> array2 = new StandardFeatureArray<>(array);
		StandardFeatureArray<Double> array3 = new StandardFeatureArray<>(array);
		
		array1.set(0, -1.0);
		array2.set(0, 3.0);
		
		assertEquals(1, array.compareTo(array1));
		assertEquals(0, array.compareTo(array3));
		assertEquals(-1, array.compareTo(array2));
	}
	
	@Test
	void compareToException() {
		assertThrows(IllegalArgumentException.class, () -> array.compareTo(
				new StandardFeatureArray<>(0.0, EmptyFeatureModel.DOUBLE)));
	}
	
	@Test
	void compareToNulls() {
		FeatureArray<Double> array1 = new StandardFeatureArray<>(0.0, model);
		FeatureArray<Double> array2 = new StandardFeatureArray<>(NULL, model);
		FeatureArray<Double> array3 = new StandardFeatureArray<>(array);
		
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
		assertEquals(array, new StandardFeatureArray<>(1.0, model));
		assertNotEquals(array, new StandardFeatureArray<>(-1.0, model));

	}
	
	@Test
	void iterator() {
		
	}

	@Test
	void getFeatureModel() {
		assertEquals(model, array.getFeatureModel());
	}

}
