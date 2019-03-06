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

import lombok.NonNull;
import org.didelphis.structures.Suppliers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 4/28/17.
 */
class SymmetricalTwoKeyMultiMapTest {
	
	private SymmetricalTwoKeyMultiMap<String, String> map;
	private SymmetricalTwoKeyMultiMap<String, String> map1;
	private SymmetricalTwoKeyMultiMap<String, String> map2;

	@BeforeEach
	void init() {
		map = new SymmetricalTwoKeyMultiMap<>();
		map.add("a", "b", "v1");
		map.add("a", "b", "v2");
		map.add("a", "b", "v3");
		map.add("a", "c", "x1");
		map.add("a", "c", "x2");
		map.add("d", "e", "y");
		
		map1 = copy(map);
		map2 = copy(map);
		map2.removeKeys("d", "e");
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

	@NonNull
	private static <K,V> SymmetricalTwoKeyMultiMap<K, V> copy(
			SymmetricalTwoKeyMultiMap<K, V> map) {
		return new SymmetricalTwoKeyMultiMap<>(map,
				new HashMap<>(),
				Suppliers.ofHashMap(),
				Suppliers.ofHashSet()
		);
	}
}
