/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.structures.maps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
		
		map1 = new SymmetricalTwoKeyMultiMap<>(map);
		map2 = new SymmetricalTwoKeyMultiMap<>(map);
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

}
