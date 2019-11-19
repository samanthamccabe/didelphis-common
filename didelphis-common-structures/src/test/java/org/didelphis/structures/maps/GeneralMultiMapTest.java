/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.structures.maps;

import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.structures.tuples.Tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings ("rawtypes")
class GeneralMultiMapTest {

	private static final Class<HashMap> MAP_TYPE = HashMap.class;
	private static final Class<HashSet> SET_TYPE = HashSet.class;

	MultiMap<String, String> map;
	MultiMap<String, String> map1;

	@BeforeEach
	void init() {
		map = new GeneralMultiMap<>();
		map.add("X", "a");
		map.add("Y", "b");
		map.add("Y", "c");
		map.add("Z", "d");
		map.add("Z", "e");
		map.add("Z", "f");

		map1 = new GeneralMultiMap<>(MAP_TYPE, SET_TYPE, map);
		map1.add("X", "1");
		map1.add("X", "2");
	}

	@Test
	void get() {
		Set<String> expectedX = new HashSet<>(Collections.singletonList("a"));
		Set<String> expectedY = new HashSet<>(Arrays.asList("b", "c"));
		Set<String> expectedZ = new HashSet<>(Arrays.asList("d", "e", "f"));

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
		assertEquals(expected, map.keys());
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
		assertEquals(new HashSet<>(Arrays.asList("a", "1", "2", "3")), map.get("X"));
	}

	@Test
	void size() {
		assertEquals(3, map.size()); // 3 keys
	}

	@Test
	void isEmpty() {
		assertFalse(map.isEmpty());
		assertTrue(new GeneralMultiMap<>(MAP_TYPE, SET_TYPE).isEmpty());
	}

	@Test
	void clear() {
		assertFalse(map.isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	void iterator() {
		Iterator<Tuple<String, Collection<String>>> iterator = map.iterator();
		assertTrue(iterator.hasNext());
	}

	@Test
	void equals() {
		MultiMap<?, ?> m1 = new GeneralMultiMap<>(MAP_TYPE, SET_TYPE, map);
		assertEquals(map, m1);
		assertNotEquals(map, new GeneralMultiMap<>());
		assertNotEquals(map, map1);
	}

	@Test
	void testHashCode() {
		MultiMap<?, ?> m1 = new GeneralMultiMap<>(MAP_TYPE, SET_TYPE, map);
		MultiMap<?, ?> m2 = new GeneralMultiMap<>(MAP_TYPE, SET_TYPE);

		assertEquals(map.hashCode(), m1.hashCode());
		assertNotEquals(map.hashCode(), m2.hashCode());
		assertNotEquals(map.hashCode(), map1.hashCode());
	}

	@Test
	void testToString() {
		MultiMap<?, ?> m1 = new GeneralMultiMap<>(MAP_TYPE, SET_TYPE, map);
		MultiMap<?, ?> m2 = new GeneralMultiMap<>(MAP_TYPE, SET_TYPE);

		assertEquals(map.toString(), m1.toString());
		assertNotEquals(map.toString(), m2.toString());
		assertNotEquals(map.toString(), map1.toString());
	}

}
