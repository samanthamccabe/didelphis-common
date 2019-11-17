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


@DisplayName("Tests Couples with a variety of parameter types")
class CoupleTest {

	@Nested
	@DisplayName("Tests on a Couple with Integer and String elements")
	class IntegerStringCoupleTest {

		private final Couple<Integer, String> tuple = new Couple<>(7, "Z");

		@Test
		void getLeft() {
			assertEquals(7, tuple.getLeft());
		}

		@Test
		void getRight() {
			assertEquals("Z", tuple.getRight());
		}

		@Test
		void testHashCode() {
			Tuple<Integer, String> tuple1 = new Couple<>(tuple);
			Tuple<Integer, String> tuple2 = new Couple<>(7, "!");

			assertEquals(tuple.hashCode(), tuple1.hashCode());
			assertNotEquals(tuple2.hashCode(), tuple.hashCode());
		}

		@Test
		void testEquals() {
			Tuple<Integer, String> tuple1 = new Couple<>(tuple);
			Tuple<Integer, String> tuple2 = new Couple<>(7, "!");

			assertEquals(tuple, tuple1);
			assertNotEquals(tuple2, tuple);
		}

		@Test
		void testToString() {
			Tuple<Integer, String> tuple1 = new Couple<>(tuple);
			Tuple<Integer, String> tuple2 = new Couple<>(7, "!");

			assertEquals(tuple.toString(), tuple1.toString());
			assertNotEquals(tuple2.toString(), tuple.toString());
		}

		@Test
		void testContains() {
			assertTrue(tuple.contains(7));
			assertTrue(tuple.contains("Z"));
			assertFalse(tuple.contains(1));
			assertFalse(tuple.contains("z"));
			assertFalse(tuple.contains("7"));
		}
	}

	@Nested
	@DisplayName("Tests on a Couple with only String elements")
	class StringCoupleTest {

		private final Couple<String, String> tuple = new Couple<>("x", "Y");

		@Test
		void getLeft() {
			assertEquals("x", tuple.getLeft());
		}

		@Test
		void getRight() {
			assertEquals("Y", tuple.getRight());
		}

		@Test
		void testHashCode() {
			Tuple<String, String> tuple1 = new Couple<>(tuple);
			Tuple<String, String> tuple2 = new Couple<>("y", "Y");

			assertEquals(tuple.hashCode(), tuple1.hashCode());
			assertNotEquals(tuple2.hashCode(), tuple.hashCode());
		}

		@Test
		void testEquals() {
			Tuple<String, String> tuple1 = new Couple<>(tuple);
			Tuple<String, String> tuple2 = new Couple<>("y", "Y");

			assertEquals(tuple, tuple1);
			assertNotEquals(tuple2, tuple);
		}

		@Test
		void testToString() {
			Tuple<String, String> tuple1 = new Couple<>(tuple);
			Tuple<String, String> tuple2 = new Couple<>("y", "Y");

			assertEquals(tuple.toString(), tuple1.toString());
			assertNotEquals(tuple2.toString(), tuple.toString());
		}

		@Test
		void testContains() {
			assertTrue(tuple.contains("x"));
			assertTrue(tuple.contains("Y"));
			assertFalse(tuple.contains("y"));
			assertFalse(tuple.contains("X"));
		}
	}

}
