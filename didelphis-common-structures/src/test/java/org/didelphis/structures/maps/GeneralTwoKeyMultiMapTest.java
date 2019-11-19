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

import org.didelphis.structures.maps.interfaces.TwoKeyMultiMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class GeneralTwoKeyMultiMapTest
		extends TwoKeyMapTestBase<String, String, Collection<String>> {

	private TwoKeyMultiMap<String, String, String> map;
	private TwoKeyMultiMap<String, String, String> map1;
	private TwoKeyMultiMap<String, String, String> map2;

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

		map1 = new GeneralTwoKeyMultiMap<>(HashMap.class, HashSet.class, map);
		map2 = new GeneralTwoKeyMultiMap<>(HashMap.class, HashSet.class, map);
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
