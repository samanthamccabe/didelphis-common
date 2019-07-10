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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 4/22/17.
 */
class CoupleTest {

	private static Couple<String, String> tuple;

	@BeforeAll
	static void init() {
		tuple = new Couple<>("x", "Y");
	}

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
		Tuple<String,String> tuple1 = new Couple<>(tuple);
		Tuple<String,String> tuple2 = new Couple<>("y", "Y");

		assertEquals(tuple.hashCode(), tuple1.hashCode());
		assertNotEquals(tuple2.hashCode(), tuple.hashCode());
	}

	@Test
	void testEquals() {
		Tuple<String,String> tuple1 = new Couple<>(tuple);
		Tuple<String,String> tuple2 = new Couple<>("y", "Y");

		assertEquals(tuple, tuple1);
		assertNotEquals(tuple2, tuple);
	}

	@Test
	void testToString() {
		Tuple<String,String> tuple1 = new Couple<>(tuple);
		Tuple<String,String> tuple2 = new Couple<>("y", "Y");

		assertEquals(tuple.toString(), tuple1.toString());
		assertNotEquals(tuple2.toString(), tuple.toString());
	}

}
