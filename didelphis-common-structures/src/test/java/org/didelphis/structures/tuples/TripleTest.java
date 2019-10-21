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

package org.didelphis.structures.tuples;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName ("Tests Triples with a variety of parameter types")
class TripleTest {

	@Nested
	@DisplayName("Tests a Triple consisting only of strings")
	class StringTripleTest {

		private final Triple<String, String, String> triple;

		StringTripleTest() {
			triple = new Triple<>("X", "Y", "Z");
		}

		@Test
		void getFirstElement() {
			String element = triple.first();
			assertEquals("X",element);
		}

		@Test
		void getSecondElement() {
			String element = triple.second();
			assertEquals("Y",element);
		}

		@Test
		void getThirdElement() {
			String element = triple.third();
			assertEquals("Z",element);
		}

		@Test
		void testEquals() {
			Triple<String, String, String> expected = triple;
			assertEquals(expected, triple);
			Triple<String, String, String> map1 = new Triple<>("X", "Y", "X");
			assertNotEquals(expected, map1);
		}

		@Test
		void testHashCode() {
			Triple<String, String, String> expected = triple;
			assertEquals(expected.hashCode(), triple.hashCode());
			Triple<String, String, String> map1 = new Triple<>("X", "Y", "X");
			assertNotEquals(expected.hashCode(), map1.hashCode());
		}

		@Test
		void testToString() {
			assertEquals("<X, Y, Z>", triple.toString());
		}
	}

	@Nested
	@DisplayName("Tests a Triple consisting only of strings")
	class StringIntegerTripleTest {

		private final Triple<String, Integer, Integer> triple;

		StringIntegerTripleTest() {
			triple = new Triple<>("X", 0, 1);
		}

		@Test
		void getFirstElement() {
			String element = triple.first();
			assertEquals("X",element);
		}

		@Test
		void getSecondElement() {
			assertEquals(0, triple.second());
		}

		@Test
		void getThirdElement() {
			assertEquals(1, triple.third());
		}

		@Test
		void testEquals() {
			assertEquals(triple, new Triple<>("X", 0, 1));
			assertNotEquals(triple, new Triple<>("X", 1, 1));
		}

		@Test
		void testHashCode() {
			assertEquals(triple.hashCode(), new Triple<>("X", 0, 1).hashCode());
			assertNotEquals(triple.hashCode(), new Triple<>("X", 1, 1).hashCode());
		}

		@Test
		void testToString() {
			assertEquals("<X, 0, 1>", triple.toString());
		}
	}
}
