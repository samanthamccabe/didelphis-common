package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.maps.interfaces.TwoKeyMap;
import org.didelphis.common.structures.tuples.Triple;
import org.didelphis.common.structures.tuples.Tuple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * Created by samantha on 1/16/17.
 */
class GeneralTwoKeyMapTest extends TwoKeyMapTestBase {

	@Test
	void iterator() {
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>();
		map.put("a1", "b1", "v1");
		map.put("a2", "b2", "v2");
		map.put("a3", "b3", "v3");
		
		Set<Triple<String, String, String>> expected = new HashSet<>();
		expected.add(new Triple<>("a1", "b1", "v1"));
		expected.add(new Triple<>("a2", "b2", "v2"));
		expected.add(new Triple<>("a3", "b3", "v3"));
		
		Set<Triple<String, String, String>> received = new HashSet<>();
		for (Triple<String, String, String> triple : map) {
			received.add(triple);
		}
		
		assertEquals(expected, received);
	}

	@Test
	void testPutAndGet() {
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>();
		map.put("a", "b", "c");
		
		testGet(map, "a", "b", "c");
		testNullGet(map, "b", "a");
		testNullGet(map, "b", "c");
	}

	@Test
	void testContains() {
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>();
		map.put("a1", "b1", "v1");
		map.put("a2", "b2", "v2");
		map.put("a3", "b3", "v3");

		testContains(map, "a1", "b1");
		testContains(map, "a2", "b2");
		testContains(map, "a3", "b3");

		testNotContains(map, "b1", "a1");
		testNotContains(map, "b2", "a2");
		testNotContains(map, "b3", "a3");
	}

	@Test
	void testKeys() {
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>();
		map.put("a1", "b1", "v1");
		map.put("a2", "b2", "v2");
		map.put("a3", "b3", "v3");

		Collection<Tuple<String, String>> expected = new ArrayList<>();
		expected.add(new Tuple<>("a1", "b1"));
		expected.add(new Tuple<>("a2", "b2"));
		expected.add(new Tuple<>("a3", "b3"));
		
		assertEquals(expected, map.keys(), "Unexpected Key Set");
	}
	
	@Test
	void testAssociatedKeys() {
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>();
		map.put("A", "1a", "v1a");
		
		map.put("B", "2a", "v2a");
		map.put("B", "2b", "v2b");
		
		map.put("C", "3a", "v3a");
		map.put("C", "3b", "v3b");
		map.put("C", "3c", "v3c");
		
		assertEquals(1,map.getAssociatedKeys("A").size());
		assertEquals(2,map.getAssociatedKeys("B").size());
		assertEquals(3,map.getAssociatedKeys("C").size());
		assertNull(map.getAssociatedKeys("X"));
	}
	
	@Test
	void testEquals() {
		GeneralTwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>();
		map.put("a1", "b1", "v1");
		map.put("a2", "b2", "v2");
		map.put("a3", "b3", "v3");

		GeneralTwoKeyMap<String, String, String> map1 = copy(map);
		GeneralTwoKeyMap<String, String, String> map2 = copy(map);
		map2.put("X", "Y", "Z");
		
		assertEquals(map, map1);
		assertNotEquals(map, map2);
	}

	@Test
	void testToString() {
		GeneralTwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>();
		map.put("a1", "b1", "v1");
		map.put("a2", "b2", "v2");
		map.put("a3", "b3", "v3");

		GeneralTwoKeyMap<String, String, String> map1 = copy(map);
		GeneralTwoKeyMap<String, String, String> map2 = copy(map);
		map2.put("X", "Y", "Z");

		assertEquals(map.toString(), map1.toString());
		assertNotEquals(map.toString(), map2.toString());
	}

	@Test
	void testHashCode() {
		GeneralTwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>();
		map.put("a1", "b1", "v1");
		map.put("a2", "b2", "v2");
		map.put("a3", "b3", "v3");

		GeneralTwoKeyMap<String, String, String> map1 = copy(map);
		GeneralTwoKeyMap<String, String, String> map2 = copy(map);
		map2.put("X", "Y", "Z");

		assertEquals(map.hashCode(), map1.hashCode());
		assertNotEquals(map.hashCode(), map2.hashCode());
	}

	private static GeneralTwoKeyMap<String, String, String> copy(
			GeneralTwoKeyMap<String, String, String> map) {
		return new GeneralTwoKeyMap<>(map);
	}
}
