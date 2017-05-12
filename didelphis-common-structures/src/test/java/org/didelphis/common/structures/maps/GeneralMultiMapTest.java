package org.didelphis.common.structures.maps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by samantha on 5/5/17.
 */
class GeneralMultiMapTest {
	
	private GeneralMultiMap<String, String> map;
	
	@BeforeEach
	void init() {
		map = new GeneralMultiMap<>();
		map.add("X", "a");
		map.add("Y", "b");
		map.add("Y", "c");
		map.add("Z", "d");
		map.add("Z", "e");
		map.add("Z", "f");
	}
	
	@Test
	void get() {
		Set<String> expectedX = new HashSet<>(Collections.singletonList("a"));
		Set<String> expectedY = new HashSet<>(Arrays.asList("b", "c"));
		Set<String> expectedZ = new HashSet<>(Arrays.asList("d", "e","f"));

		assertEquals(expectedX, map.get("X"));
		assertEquals(expectedY, map.get("Y"));
		assertEquals(expectedZ, map.get("Z"));
		
		assertNull(map.get("W"));
	}

	@Test
	void containsKey() {
		assertTrue(map.containsKey("X"));
		assertTrue(map.containsKey("Y"));
		assertTrue(map.containsKey("Z"));
		assertFalse(map.containsKey("W"));
		assertFalse(map.containsKey(null));
	}

	@Test
	void keys() {
		Set<String> expected = new HashSet<>(Arrays.asList("X", "Y", "Z"));
		assertEquals(expected,  map.keys());
	}

	@Test
	void remove() {
		Set<String> expected = new HashSet<>(Arrays.asList("b", "c"));
		Collection<String> received = map.remove("Y");
		
		assertEquals(expected, received);
		assertFalse(map.containsKey("Y"));
	}

	@Test
	void add() {
		map.add("X", "x");
		Set<String> expected = new HashSet<>(Arrays.asList("a", "x"));
		assertEquals(expected, map.get("X"));
	}

	@Test
	void addNullValue() {
		map.add("X", null);
		Set<String> expected = new HashSet<>(Arrays.asList("a", null));
		assertEquals(expected, map.get("X"));
	}

	@Test
	void addNullKey() {
		map.add(null, "1");
		map.add(null, "2");
		Set<String> expected = new HashSet<>(Arrays.asList("1", "2"));
		assertEquals(expected, map.get(null));
	}

	@Test
	void addAll() {
		map.addAll("X", Arrays.asList("1", "2", "3"));
		assertEquals(
				new HashSet<>(Arrays.asList("a", "1", "2", "3")),
		        map.get("X")
		);
	}

	@Test
	void getDelegate() {
		Map<String, Collection<String>> delegate = new HashMap<>();
		delegate.put("X", new HashSet<>(Arrays.asList("a")));
		delegate.put("Y", new HashSet<>(Arrays.asList("b", "c")));
		delegate.put("Z", new HashSet<>(Arrays.asList("d", "e", "f")));
		assertEquals(delegate, map.getDelegate());
	}

	@Test
	void size() {
		assertEquals(6, map.size());
	}

	@Test
	void isEmpty() {
	}

	@Test
	void clear() {
	}

	@Test
	void iterator() {
	}

	@Test
	void equals() {
	}

	@Test
	void testHashCode() {
	}

	@Test
	void testToString() {
	}

}
