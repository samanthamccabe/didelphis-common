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

import static org.junit.jupiter.api.Assertions.*;


class TwoKeyMapTestBase<T,U,V> {

	void testGet(TwoKeyMap<T, U, V> map, T k1, U k2, V v) {
		assertEquals(v, map.get(k1, k2), () ->
				"key (" + k1 + ", " + k2 + ") retrieved unexpected value");
	}

	void testNullGet(TwoKeyMap<T, U, V> map, T k1, U k2) {
		assertNull(map.get(k1, k2), () ->
				"key (" + k1 + ", " + k2 + ") should not exist");
	}

	void testContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		assertTrue(map.contains(k1, k2), () ->
				"key (" + k1 + ", " + k2 + ") should be found in maps");
	}

	void testNotContains(TwoKeyMap<T, U, V> map, T k1, U k2) {
		assertFalse(map.contains(k1, k2), () ->
				"key (" + k1 + ", " + k2 + ") should *not* be found in maps");
	}
}
