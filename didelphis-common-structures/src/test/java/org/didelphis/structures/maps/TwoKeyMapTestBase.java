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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by samantha on 1/16/17.
 */
public abstract class TwoKeyMapTestBase {
	
	protected TwoKeyMapTestBase() {}
	
	static <T, U, V> void testGet(TwoKeyMap<T, U, V> map, T k1, U k2, V v) {
		String string = "key (" + k1 + ',' + k2 + ") retrieved unexpected value";
		assertEquals(v, map.get(k1, k2), string);
	}

	static <T, U, V> void testNullGet(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String string = "key (" + k1 + ',' + k2 + ") should not exist";
		assertNull(map.get(k1, k2), string);
	}

	static <T, U, V> void testContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String string = "key (" + k1 + ',' + k2 + ") should be found in maps";
		assertTrue(map.contains(k1, k2),string);
	}

	static <T, U, V> void testNotContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		String string = "key (" + k1 + ',' + k2 + ") should *not* be found in maps";
		assertFalse(map.contains(k1, k2),string);
	}
}
