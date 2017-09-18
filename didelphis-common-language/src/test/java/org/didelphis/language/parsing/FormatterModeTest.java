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

package org.didelphis.language.parsing;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by samantha on 4/15/17.
 */
class FormatterModeTest {

	@Test
	void normalizeNone() {
		String string = "string";
		assertEquals(string, FormatterMode.NONE.normalize(string));
	}

	@Test
	void splitNone() {
		String string = "string";
		assertEquals(
				Arrays.asList("s","t","r","i","n","g"),
				FormatterMode.NONE.split(string));
	}

	@Test
	void normalizeDecomposition() {
		String string = "răs";
		assertEquals("ra\u0306s", FormatterMode.DECOMPOSITION.normalize(string));
	}

	@Test
	void splitDecomposition() {
		String string = "răs";
		assertEquals(
				Arrays.asList("r","a","\u0306","s"),
				FormatterMode.DECOMPOSITION.split(string));
	}

	@Test
	void normalizeComposition() {
		String string = "ra\u0306s";
		assertEquals("răs", FormatterMode.COMPOSITION.normalize(string));
	}

	@Test
	void splitComposition() {
		String string = "ra\u0306s";
		assertEquals(
				Arrays.asList("r","ă","s"),
				FormatterMode.COMPOSITION.split(string));
	}

	@Test
	void normalizeIntelligent() {
		String string = "răs";
		assertEquals("ra\u0306s", FormatterMode.INTELLIGENT.normalize(string));
	}

	@Test
	void splitIntelligent() {
		assertEquals(Arrays.asList("r","a\u0306","s"),
				FormatterMode.INTELLIGENT.split("răs"));

		assertEquals(Arrays.asList("tˀ","a\u0306","s"),
				FormatterMode.INTELLIGENT.split("tˀăs"));
	}
	
	@Test
	void valueOf() {
		assertSame(FormatterMode.INTELLIGENT, FormatterMode.valueOf("INTELLIGENT"));
		assertSame(FormatterMode.DECOMPOSITION, FormatterMode.valueOf("DECOMPOSITION"));
		assertSame(FormatterMode.COMPOSITION, FormatterMode.valueOf("COMPOSITION"));
		assertSame(FormatterMode.NONE, FormatterMode.valueOf("NONE"));

		assertThrows(IllegalArgumentException.class,()->FormatterMode.valueOf("foo"));
	}

}
