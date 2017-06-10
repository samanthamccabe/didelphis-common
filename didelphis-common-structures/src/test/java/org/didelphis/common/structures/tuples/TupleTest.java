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

package org.didelphis.common.structures.tuples;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by samantha on 4/22/17.
 */
class TupleTest {

	private static Tuple<String, String> tuple;

	@BeforeAll
	static void init() {
		tuple = new Tuple<>("x", "Y");
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
		Tuple<String, String> tuple1 = new Tuple<>(tuple);
		Tuple<String, String> tuple2 = new Tuple<>("y", "Y");

		assertEquals(tuple.hashCode(), tuple1.hashCode());
		assertNotEquals(tuple2.hashCode(), tuple.hashCode());
	}

	@Test
	void testEquals() {
		Tuple<String, String> tuple1 = new Tuple<>(tuple);
		Tuple<String, String> tuple2 = new Tuple<>("y", "Y");

		assertEquals(tuple, tuple1);
		assertNotEquals(tuple2, tuple);
	}

	@Test
	void testToString() {
		Tuple<String, String> tuple1 = new Tuple<>(tuple);
		Tuple<String, String> tuple2 = new Tuple<>("y", "Y");

		assertEquals(tuple.toString(), tuple1.toString());
		assertNotEquals(tuple2.toString(), tuple.toString());
	}

}
