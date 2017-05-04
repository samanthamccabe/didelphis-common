package org.didelphis.common.structures.tuples;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 4/22/17.
 */
class TupleTest {

	private static Tuple<String, String> tuple;

	@BeforeAll
	static void init() {
		tuple = new Tuple<>("x", "Y");
	}

	@Test
	void getLeft() {
		assertEquals("x", tuple.getLeft());
	}

	@Test
	void getRight() {
		assertEquals("Y", tuple.getRight());
	}

	@Test
	void testHashCode() {
		Tuple<String, String> tuple1 = new Tuple<>(tuple);
		Tuple<String, String> tuple2 = new Tuple<>("y", "Y");

		assertEquals(tuple.hashCode(), tuple1.hashCode());
		assertNotEquals(tuple2.hashCode(), tuple.hashCode());
	}

	@Test
	void testEquals() {
		Tuple<String, String> tuple1 = new Tuple<>(tuple);
		Tuple<String, String> tuple2 = new Tuple<>("y", "Y");

		assertEquals(tuple, tuple1);
		assertNotEquals(tuple2, tuple);
	}

	@Test
	void testToString() {
		Tuple<String, String> tuple1 = new Tuple<>(tuple);
		Tuple<String, String> tuple2 = new Tuple<>("y", "Y");

		assertEquals(tuple.toString(), tuple1.toString());
		assertNotEquals(tuple2.toString(), tuple.toString());
	}

}
