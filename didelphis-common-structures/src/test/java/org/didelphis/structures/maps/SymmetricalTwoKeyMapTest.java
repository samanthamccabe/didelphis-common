/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.structures.maps;

import org.didelphis.structures.maps.interfaces.TwoKeyMap;
import org.didelphis.structures.tuples.Triple;
import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by samantha on 4/9/17.
 */
class SymmetricalTwoKeyMapTest {
	
	private SymmetricalTwoKeyMap<String, Integer> map;
	
	@BeforeEach
	void init() {
		map = new SymmetricalTwoKeyMap<>();
		map.put("A", "B", 1);
		map.put("A", "C", 2);
		map.put("B", "C", 3);
		map.put("A", "D", 4);
		map.put("B", "D", 5);
		map.put("C", "D", 6);
	}
	
	@Test
	void size() {
		assertEquals(6, map.size());
	}

	@Test
	void isEmpty() {
		assertFalse(map.isEmpty());
		assertTrue(new SymmetricalTwoKeyMap().isEmpty());
	}

	@Test
	void containsKey() {
		assertTrue(map.contains("A", "B"));
		assertTrue(map.contains("B", "C"));
		assertTrue(map.contains("C", "D"));
		
		assertFalse(map.contains("C", "C"));
	}
	
	@Test
	void get() {
		assertEquals(1, (int) map.get("A", "B"));
		assertEquals(2, (int) map.get("A", "C"));
		assertEquals(3, (int) map.get("B", "C"));
		assertEquals(4, (int) map.get("A", "D"));
		assertEquals(5, (int) map.get("B", "D"));
		assertEquals(6, (int) map.get("C", "D"));
		//
		assertEquals(1, (int) map.get("B", "A"));
		assertEquals(2, (int) map.get("C", "A"));
		assertEquals(3, (int) map.get("C", "B"));
		assertEquals(4, (int) map.get("D", "A"));
		assertEquals(5, (int) map.get("D", "B"));
		assertEquals(6, (int) map.get("D", "C"));
		
		//
		assertNull(map.get("X", "X"));
	}
	
	@Test
	void put() {
		// E = 7
		map.put("A", "E", 7);
		map.put("B", "E", 8);
		map.put("C", "E", 9);
		map.put("X", "Y", 0);

		assertEquals(7, (int) map.get("A", "E"));
		assertEquals(8, (int) map.get("B", "E"));
		assertEquals(9, (int) map.get("C", "E"));
		//
		assertEquals(7, (int) map.get("E", "A"));
		assertEquals(8, (int) map.get("E", "B"));
		assertEquals(9, (int) map.get("E", "C"));
		//
		assertEquals(0, (int) map.get("X", "Y"));
		assertEquals(0, (int) map.get("Y", "X"));
	}

	@Test
	void putReverse() {
		// E = 7
		map.put("E", "A", 7);
		map.put("E", "B", 8);
		map.put("E", "C", 9);
		map.put("Y", "X", 0);

		assertEquals(7, (int) map.get("A", "E"));
		assertEquals(8, (int) map.get("B", "E"));
		assertEquals(9, (int) map.get("C", "E"));
		//
		assertEquals(7, (int) map.get("E", "A"));
		assertEquals(8, (int) map.get("E", "B"));
		assertEquals(9, (int) map.get("E", "C"));
		//
		assertEquals(0, (int) map.get("X", "Y"));
		assertEquals(0, (int) map.get("Y", "X"));
	}

	@Test
	void contains() {
		assertTrue(map.contains("A", "B"));
		assertTrue(map.contains("A", "C"));
		assertTrue(map.contains("B", "C"));
		assertTrue(map.contains("A", "D"));
		assertTrue(map.contains("B", "D"));
		assertTrue(map.contains("C", "D"));
		//
		assertTrue(map.contains("B", "A"));
		assertTrue(map.contains("C", "A"));
		assertTrue(map.contains("C", "B"));
		assertTrue(map.contains("D", "A"));
		assertTrue(map.contains("D", "B"));
		assertTrue(map.contains("D", "C"));
		//
		assertFalse(map.contains("X", "X"));
	}
	
	@Test
	void keys() {
		Collection<Tuple<String,String>> expected = new ArrayList<>();
		expected.add(new Couple<>("A", "B"));
		expected.add(new Couple<>("A", "C"));
		expected.add(new Couple<>("A", "D"));
		expected.add(new Couple<>("B", "C"));
		expected.add(new Couple<>("B", "D"));
		expected.add(new Couple<>("C", "D"));

		try {
			HashMap<String, String> hashMap = new HashMap<>();
			hashMap.put("X", "Y");
			
			@SuppressWarnings("unchecked")
			Map<String, String> map = HashMap.class
					.getConstructor(Map.class)
					.newInstance(hashMap);
			
			assertEquals("Y", map.get("X"));
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//			e.printStackTrace();
		}

		assertEquals(expected, map.keys());
	}
	
	@Test
	void iterator() {
		Collection<Triple<String, String, Integer>> expected = new HashSet<>();
		expected.add(new Triple<>("A", "B", 1));
		expected.add(new Triple<>("A", "C", 2));
		expected.add(new Triple<>("B", "C", 3));
		expected.add(new Triple<>("A", "D", 4));
		expected.add(new Triple<>("B", "D", 5));
		expected.add(new Triple<>("C", "D", 6));

		Collection<Triple<String, String, Integer>> received = new HashSet<>();
		map.iterator().forEachRemaining(received::add);
		
		assertEquals(expected, received);
	}

	@Test
	void clear() {
		TwoKeyMap<String,String, Integer> keyMap = new SymmetricalTwoKeyMap<>(map);
		keyMap.clear();
		assertTrue(keyMap.isEmpty());
	}

	@Test
	void testEquals() {
		assertEquals(map, new SymmetricalTwoKeyMap<>(map));
		SymmetricalTwoKeyMap<String, Integer> map1 = new SymmetricalTwoKeyMap<>(map);
		map1.put("X", "Y", 333);
		assertNotEquals(map, map1);
	}
	
	@Test
	void testToString() {
		assertEquals(map.toString(), new SymmetricalTwoKeyMap<>(map).toString());
		SymmetricalTwoKeyMap<String, Integer> map1 = new SymmetricalTwoKeyMap<>(map);
		map1.put("X", "Y", 333);
		assertNotEquals(map.toString(), map1.toString());
	}

	@Test
	void testHashCode() {
		assertEquals(map.hashCode(), new SymmetricalTwoKeyMap<>(map).hashCode());
		SymmetricalTwoKeyMap<String, Integer> map1 = new SymmetricalTwoKeyMap<>(map);
		map1.put("X", "Y", 333);
		assertNotEquals(map.hashCode(), map1.hashCode());
	}
}
