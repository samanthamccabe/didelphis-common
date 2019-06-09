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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class TripleTest {
	
	@Test
	void getFirstElement() {
		String element = new Triple<>("X", "Y", "Z").getFirstElement();
		assertEquals("X",element);
	}

	@Test
	void getSecondElement() {
		String element = new Triple<>("X", "Y", "Z").getSecondElement();
		assertEquals("Y",element);
	}

	@Test
	void getThirdElement() {
		String element = new Triple<>("X", "Y", "Z").getThirdElement();
		assertEquals("Z",element);
	}

	@Test
	void testEquals() {
		Triple<String, String, String> expected = new Triple<>("X", "Y", "Z");
		assertEquals(expected, new Triple<>("X","Y","Z"));
		Triple<String, String, String> map1 = new Triple<>("X", "Y", "X");
		assertNotEquals(expected, map1);
	}

	@Test
	void testHashCode() {
		Triple<String, String, String> expected = new Triple<>("X", "Y", "Z");
		assertEquals(expected.hashCode(), new Triple<>("X","Y","Z").hashCode());
		Triple<String, String, String> map1 = new Triple<>("X", "Y", "X");
		assertNotEquals(expected.hashCode(), map1.hashCode());
	}

	@Test
	void testToString() {
		assertEquals("<X, Y, Z>", new Triple<>("X","Y","Z").toString());
	}

}
