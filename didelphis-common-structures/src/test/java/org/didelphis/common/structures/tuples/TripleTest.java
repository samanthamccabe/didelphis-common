package org.didelphis.common.structures.tuples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 4/3/17.
 */
class TripleTest {
	
	@Test
	void getFirstElement() {
		String element = new Triple<>("X", "Y", "Z").getFirstElement();
		assertEquals("X",element);
	}

	@Test
	void getSecondElement() {
		String element = new Triple<>("X", "Y", "Z").getSecondElement();
		assertEquals("Y",element);
	}

	@Test
	void getThirdElement() {
		String element = new Triple<>("X", "Y", "Z").getThirdElement();
		assertEquals("Z",element);
	}

	@Test
	void testEquals() {
		Triple<String, String, String> expected = new Triple<>("X", "Y", "Z");
		assertEquals(expected, new Triple<>("X","Y","Z"));
		Triple<String, String, String> map1 = new Triple<>("X", "Y", "X");
		assertNotEquals(expected, map1);
	}

	@Test
	void testHashCode() {
		Triple<String, String, String> expected = new Triple<>("X", "Y", "Z");
		assertEquals(expected.hashCode(), new Triple<>("X","Y","Z").hashCode());
		Triple<String, String, String> map1 = new Triple<>("X", "Y", "X");
		assertNotEquals(expected.hashCode(), map1.hashCode());
	}

	@Test
	void testToString() {
		assertEquals("<X, Y, Z>", new Triple<>("X","Y","Z").toString());
	}

}
