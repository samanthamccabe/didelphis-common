package org.didelphis.structures.tuples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwinTest {

	@Test
	void size() {
		assertEquals(2, new Twin<>("", "").size());
	}

	@Test
	void testToString() {
		assertEquals(
				new Twin<>(1, 2).toString(),
				new Twin<>(1, 2).toString()
		);
		assertEquals(
				new Twin<>("1", "2").toString(),
				new Twin<>("1", "2").toString()
		);
		assertEquals(
				new Twin<>(Boolean.TRUE, Boolean.FALSE).toString(),
				new Twin<>(Boolean.TRUE, Boolean.FALSE).toString()
		);
	}

	@Test
	void get() {
		Twin<String> strings = new Twin<>("a", "b");

		assertEquals("a", strings.get(0));
		assertEquals("b", strings.get(1));

		assertThrows(IndexOutOfBoundsException.class, () -> strings.get(2));
	}

	@Test
	void getLeft() {
		Tuple<String, String> strings = new Twin<>("a", "b");
		assertEquals("a", strings.getLeft());
	}

	@Test
	void getRight() {
		Tuple<String, String> strings = new Twin<>("a", "b");
		assertEquals("b", strings.getRight());
	}

	@Test
	void testEquals() {
		assertEquals(
				new Twin<>(1, 2),
				new Twin<>(1, 2)
		);
		assertEquals(
				new Twin<>("1", "2"),
				new Twin<>("1", "2")
		);
		assertEquals(
				new Twin<>(Boolean.TRUE, Boolean.FALSE),
				new Twin<>(Boolean.TRUE, Boolean.FALSE)
		);
	}

	@Test
	void testHashCode() {
		assertEquals(
				new Twin<>(1, 2).hashCode(),
				new Twin<>(1, 2).hashCode()
		);
		assertEquals(
				new Twin<>("1", "2").hashCode(),
				new Twin<>("1", "2").hashCode()
		);
		assertEquals(
				new Twin<>(Boolean.TRUE, Boolean.FALSE).hashCode(),
				new Twin<>(Boolean.TRUE, Boolean.FALSE).hashCode()
		);
	}
}
