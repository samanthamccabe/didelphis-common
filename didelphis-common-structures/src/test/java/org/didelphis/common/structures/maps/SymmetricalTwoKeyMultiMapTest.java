package org.didelphis.common.structures.maps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 4/28/17.
 */
class SymmetricalTwoKeyMultiMapTest {
	
	private SymmetricalTwoKeyMultiMap<String, String> map;
	private SymmetricalTwoKeyMultiMap<String, String> map1;
	private SymmetricalTwoKeyMultiMap<String, String> map2;

	@SuppressWarnings("unchecked")
	@BeforeEach
	void init() {
		map = new SymmetricalTwoKeyMultiMap<>();
		map.add("a", "b", "v1");
		map.add("a", "b", "v2");
		map.add("a", "b", "v3");
		map.add("a", "c", "x1");
		map.add("a", "c", "x2");
		map.add("d", "e", "y");
		
		map1 = new SymmetricalTwoKeyMultiMap<>(map, HashSet.class);
		map2 = new SymmetricalTwoKeyMultiMap<>(map, HashSet.class);
		map2.remove("d", "e");
	}
	
	@Test
	void add() {
		map.add("a", "b", "z");
		
		assertEquals(4, map.get("a", "b").size());
		assertEquals(4, map.get("b", "a").size());
	}

	@Test
	void equals() {
		assertEquals(map, map1);
		assertNotEquals(map, map2);
	}

	@Test
	void testHashCode() {
		assertEquals(map.hashCode(), map1.hashCode());
		assertNotEquals(map.hashCode(), map2.hashCode());
	}
	
	@Test
	void testToString() {
		assertEquals(map.toString(), map1.toString());
		assertNotEquals(map.toString(), map2.toString());
	}

}
