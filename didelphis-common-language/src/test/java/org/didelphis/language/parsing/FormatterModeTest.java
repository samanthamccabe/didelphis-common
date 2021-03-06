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

package org.didelphis.language.parsing;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.*;
import static org.didelphis.language.parsing.FormatterMode.*;
import static org.junit.jupiter.api.Assertions.*;


class FormatterModeTest {

	@Test
	void normalizeNone() {
		String string = "string";
		assertEquals(string, NONE.normalize(string));
	}

	@Test
	void splitNone() {
		String string = "string";
		List<String> expected = asList("s", "t", "r", "i", "n", "g");
		List<String> received = NONE.split(string);
		assertEquals(expected, received);
	}

	@Test
	void normalizeDecomposition() {
		String string = "răs";
		assertEquals("ra\u0306s", DECOMPOSITION.normalize(string));
	}

	@Test
	void splitDecomposition() {
		String string = "răs";
		List<String> expected = asList("r", "a", "\u0306", "s");
		assertEquals(expected, DECOMPOSITION.split(string));
	}

	@Test
	void normalizeComposition() {
		String string = "ra\u0306s";
		assertEquals("răs", COMPOSITION.normalize(string));
	}

	@Test
	void splitComposition() {
		String string = "ra\u0306s";
		List<String> expected = asList("r", "ă", "s");
		List<String> received = COMPOSITION.split(string);
		assertEquals(expected, received);
	}

	@Test
	void normalizeIntelligent() {
		String string = "răs";
		assertEquals("ra\u0306s", INTELLIGENT.normalize(string));
	}

	@Test
	void splitIntelligent01() {
		List<String> expected = asList("r", "a\u0306", "s");
		List<String> received = INTELLIGENT.split("răs");
		assertEquals(expected, received);

	}

	@Test
	void splitIntelligent02() {
		List<String> expected = asList("tˀ", "a\u0306", "s");
		List<String> received = INTELLIGENT.split("tˀăs");
		assertEquals(expected, received);
	}

	@Test
	void splitIntelligent03() {
		List<String> expected = asList("r", "$C1", "s");
		List<String> received = INTELLIGENT.split("r$C1s");
		assertEquals(expected, received);
	}

	@Test
	void splitIntelligentWithParens() {

		Map<String, String> map = new HashMap<>();
		map.put("[", "]");

		List<String> expected = asList("[-voice]", "[+con, -son, -voice]");
		List<String> received = INTELLIGENT.split(
				"[-voice][+con, -son, -voice]",
				Collections.emptyList(),
				map
		);

		assertEquals(expected, received);
	}

	@Test
	void splitIntelligentBinders() {
		List<String> expected1 = asList("r", "a͜a", "s");
		List<String> received1 = INTELLIGENT.split("ra͜as");
		assertEquals(expected1, received1);

		List<String> expected2 = asList("r", "a͝a", "s");
		List<String> received2 = INTELLIGENT.split("ra͝as");
		assertEquals(expected2, received2);

		List<String> expected3 = asList("r", "a͞a", "s");
		List<String> received3 = INTELLIGENT.split("ra͞as");
		assertEquals(expected3, received3);

		List<String> expected4 = asList("r", "a͟a", "s");
		List<String> received4 = INTELLIGENT.split("ra͟as");
		assertEquals(expected4, received4);

		List<String> expected5 = asList("r", "a͠a", "s");
		List<String> received5 = INTELLIGENT.split("ra͠as");
		assertEquals(expected5, received5);

		List<String> expected6 = asList("r", "a͡a", "s");
		List<String> received6 = INTELLIGENT.split("ra͡as");
		assertEquals(expected6, received6);

		List<String> expected7 = asList("r", "a͢a", "s");
		List<String> received7 = INTELLIGENT.split("ra͢as");
		assertEquals(expected7, received7);

		List<String> expected8 = asList("r", "a͢");
		List<String> received8 = INTELLIGENT.split("ra͢");
		assertEquals(expected8, received8);
	}

	@Test
	void valueOf() {
		assertSame(INTELLIGENT,   FormatterMode.valueOf("INTELLIGENT"));
		assertSame(DECOMPOSITION, FormatterMode.valueOf("DECOMPOSITION"));
		assertSame(COMPOSITION,   FormatterMode.valueOf("COMPOSITION"));
		assertSame(NONE,          FormatterMode.valueOf("NONE"));

		assertThrows(
				IllegalArgumentException.class,
				() -> FormatterMode.valueOf("foo")
		);
	}

}
