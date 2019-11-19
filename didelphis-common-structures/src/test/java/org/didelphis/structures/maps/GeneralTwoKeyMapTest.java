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

import org.didelphis.structures.maps.interfaces.TwoKeyMap;
import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Triple;
import org.didelphis.structures.tuples.Tuple;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class GeneralTwoKeyMapTest extends TwoKeyMapTestBase<String, String, String> {

	@Test
	void testByClassConstructor() {
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>(HashMap.class);
		map.put("A", "A", "A");
		assertEquals("A", map.get("A", "A"));
	}

	@Test
	void iterator() {
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>(HashMap.class);
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
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>(HashMap.class);
		map.put("a", "b", "c");

		testGet(map, "a", "b", "c");
		testNullGet(map, "b", "a");
		testNullGet(map, "b", "c");
	}

	@Test
	void testContains() {
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>(HashMap.class);
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
		TwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>(HashMap.class);
		map.put("a1", "b1", "v1");
		map.put("a2", "b2", "v2");
		map.put("a3", "b3", "v3");

		Collection<Tuple<String, String>> expected = new ArrayList<>();
		expected.add(new Couple<>("a1", "b1"));
		expected.add(new Couple<>("a2", "b2"));
		expected.add(new Couple<>("a3", "b3"));

		assertEquals(expected, map.keys(), "Unexpected Key Set");
	}

	@Test
	void testEquals() {
		GeneralTwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>(HashMap.class);
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
		GeneralTwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>(HashMap.class);
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
		GeneralTwoKeyMap<String, String, String> map = new GeneralTwoKeyMap<>(HashMap.class);
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
			GeneralTwoKeyMap<String, String, String> map
	) {
		return new GeneralTwoKeyMap<>(HashMap.class, map);
	}
}
