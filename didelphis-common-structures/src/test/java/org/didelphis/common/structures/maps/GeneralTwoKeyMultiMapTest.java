package org.didelphis.common.structures.maps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 12/20/16.
 */
class GeneralTwoKeyMultiMapTest extends TwoKeyMapTestBase {
	
	private GeneralTwoKeyMultiMap<String, String, String> map;
	private GeneralTwoKeyMultiMap<String, String, String> map1;
	private GeneralTwoKeyMultiMap<String, String, String> map2;
	
	@BeforeEach
	void init() {
		map = new GeneralTwoKeyMultiMap<>();
		map.add("a", "b", "y1");
		map.add("a", "b", "y2");
		map.add("a", "b", "y3");

		map.add("b", "a", "x1");
		map.add("b", "a", "x2");
		map.add("b", "a", "x3");

		map.add("a", "c", "v1");
		map.add("a", "c", "v2");

		map1 = new GeneralTwoKeyMultiMap<>(map);
		map2 = new GeneralTwoKeyMultiMap<>(map);
		map2.add("x", "y", "z");
	}
	
	@Test
	void testHashCode() {
		assertEquals(map.hashCode(), map1.hashCode());
		assertNotEquals(map.hashCode(), map2.hashCode());
	}

	@Test
	void testEquals() {
		assertEquals(map, map1);
		assertNotEquals(map, map2);
	}

	@Test
	void testToString() {
		assertEquals(map.toString(), map1.toString());
		assertNotEquals(map.toString(), map2.toString());
	}
	
	@Test
	void testAdd() {
		Set<String> objects = new HashSet<>();
		objects.add("y1");
		objects.add("y2");
		objects.add("y3");
		
		testGet(map, "a", "b", objects);
		testNullGet(map, "c", "a");
		testNullGet(map, "b", "c");
	}

}
