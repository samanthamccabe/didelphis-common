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

package org.didelphis.utilities;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.*;
import static org.didelphis.utilities.Splitter.*;
import static org.junit.jupiter.api.Assertions.*;

class SplitterTest {

	private static final Map<String, String> DELIM = new HashMap<>();
	private static final Set<String>         EMPTY = Collections.emptySet();

	static {
		DELIM.put("[^", "]");
		DELIM.put("(?:", ")");
		DELIM.put("[", "]");
		DELIM.put("(", ")");
		DELIM.put("{", "}");
	}

	@Nested
	class BracketsTest {
		@Test
		void testParseParens01() {
			assertEquals(5, parseParens("[b c]", DELIM, EMPTY, 0));
		}

		@Test
		void testParseParens02() {
			assertEquals(7, parseParens("a [b c]", DELIM, EMPTY, 2));
		}

		@Test
		void testParseParens03() {
			assertEquals(11, parseParens("a [^bc[xy]]", DELIM, EMPTY, 2));
		}

		@Test
		void testParseParens04() {
			String string = "[-con, +voice, -creaky][-son, -voice, +vot]us";
			assertEquals(23, parseParens(string, DELIM, EMPTY, 0));
			assertEquals(43, parseParens(string, DELIM, EMPTY, 23));
		}

		@Test
		void testFindClosingBracketA01() {
			assertEquals(7, findBracket("a [b c]", "[", 2));
		}

		@Test
		void testFindClosingBracketB01() {
			assertEquals(7, findBracket("a [b c]", "[", 2));
		}

		@Test
		void testFindNoClosingBracket1() {
			assertEquals(-1, findBracket("a [b c", "[", 2));
		}

		@Test
		void testFindNoClosingBracket2() {
			assertEquals(-1, findBracket("a [b c[", "[", 2));
		}

		@Test
		void testFindNoClosingBracket3() {
			assertEquals(-1, findBracket("a [b c [c]", "[", 2));
		}

		@Test
		void testDanglingClosingBracket() {
			String string = "([^\\]]+)]";
			Set<String> specials = new HashSet<>();
			specials.add("\\]");
			assertEquals(8, findClosingBracket(string, "(", DELIM, specials, 0));
		}

		private int findBracket(String exp, String open, int index) {
			return findClosingBracket(exp, open, DELIM, EMPTY, index);
		}
	}

	@Nested
	class SplitWhitespaceTest {

		@Test
		void testWhitespace01() {
			String string = "a b c";
			assertEquals(asList("a", "b", "c"), whitespace(string, DELIM));
		}

		@Test
		void testWhitespace02() {
			String string = "a     b  c";
			assertEquals(asList("a", "b", "c"), whitespace(string, DELIM));
		}

		@Test
		void testWhitespaceWithBraces01() {
			String string = "a [b c]";
			List<String> expected = asList("a", "[b c]");
			List<String> received = whitespace(string, DELIM);
			assertEquals(expected, received);
		}

		@Test
		void testWhitespaceWithBraces02() {
			String string = "a [b   c]  d";
			List<String> expected = asList("a", "[b   c]", "d");
			List<String> received = whitespace(string, DELIM);
			assertEquals(expected, received);
		}

		@Test
		void testWhitespaceWithMultipleBraces01() {
			String string = "a [b   c]  d [e f]";
			List<String> expected = asList("a", "[b   c]", "d", "[e f]");
			List<String> received = whitespace(string, DELIM);
			assertEquals(expected, received);
		}

		@Test
		void testWhitespaceWithMultipleBraces02() {
			String string = "a [b   c]  [d [e f]]";
			List<String> expected = asList("a", "[b   c]", "[d [e f]]");
			List<String> received = whitespace(string, DELIM);
			assertEquals(expected, received);
		}

		@Test
		void testWhiteSpaceWithMultipleBraces03() {
			String string = "ab* (cd?)+ ((ae)*f)+";
			List<String> expected = asList("ab*", "(cd?)+", "((ae)*f)+");
			List<String> received = whitespace(string, DELIM);
			assertEquals(expected, received);
		}
	}

	@Nested
	class SplitListTest {

		@Test
		void testDanglingClosingBracket() {
			Set<String> specials = new HashSet<>();
			specials.add("\\[");

			String string = "\\[([^]]+)]";
			List<String> actual = toList(string, DELIM, specials);
			assertEquals(asList("\\[", "([^]]+)", "]"), actual);
		}

		@Test
		void testEmbedded01() {
			List<String> specials = asList(
					"\\d", "\\D", "\\w", "\\W", "\\s", "\\S", "\\a", "\\A"
			);
			List<String> strings = toList("([^'\"]+)", DELIM, specials);

			List<String> expected = Collections.singletonList("([^'\"]+)");
			assertEquals(expected, strings);
		}

		@Test
		void testEmbedded02() {
			List<String> specials = asList(
					"\\d", "\\D", "\\w", "\\W", "\\s", "\\S", "\\a", "\\A"
			);
			List<String> strings = toList("(.*[\\\\/])?[^\\\\/]+$", DELIM,
					specials
			);

			List<String> expected = asList("(.*[\\\\/])",
					"?",
					"[^\\\\/]",
					"+",
					"$");
			assertEquals(expected, strings);
		}

		@Test
		void testEscapedBrackets() {
			List<String> specials = asList("\\]", "\\[");
			String string = "\\[([^\\]]*)\\]";
			List<String> strings = toList(string, DELIM, specials);
			List<String> expected = asList("\\[", "([^\\]]*)", "\\]");
			assertEquals(expected, strings);
		}


		@Test
		void testWhiteSpaceWithSets01() {
			List<String> list = toList("{ab* (cd?)+ ((ae)*f)+}tr", DELIM, EMPTY
			);
			assertEquals(3, list.size());
		}

		@Test
		void testWhiteSpaceWithSets02() {
			List<String> list = toList(
					"{ab {cd xy} ef}tr", DELIM, EMPTY
			);
			assertEquals(3, list.size());
		}

	}

	@Nested
	class testSplitLines {

		@Test
		void testLineBreak01() {
			String string = "a\nb\nc\nd";
			List<String> expected = asList("a", "b", "c", "d");
			assertEquals(expected, lines(string));
		}

		@Test
		void testLineBreak02() {
			String string = "\na\nb\nc\nd\n";
			List<String> expected = asList("", "a", "b", "c", "d", "");
			assertEquals(expected, lines(string));
		}

		@Test
		void testLineBreak03() {
			String string = "\na\n\nb\nc\nd\n";
			List<String> expected = asList("", "a", "", "b", "c", "d", "");
			assertEquals(expected, lines(string));
		}

		@Test
		void testLineBreak04() {
			String string = "\na\r\nb\nc\nd\n";
			List<String> expected = asList("", "a", "b", "c", "d", "");
			assertEquals(expected, lines(string));
		}

		@Test
		void testLineBreak05() {
			String string = "\na\r\nb\nc\r\rd\n";
			List<String> expected = asList("", "a", "b", "c", "", "d", "");
			assertEquals(expected, lines(string));
		}

		@Test
		void testLineBreak06() {
			String string = "";
			List<String> expected = Collections.singletonList("");
			assertEquals(expected, lines(string));
		}

		@Test
		void testLineBreak07() {
			String string = "\n\r\n\n";
			List<String> expected = asList("", "", "", "");
			assertEquals(expected, lines(string));
		}
	}
}
