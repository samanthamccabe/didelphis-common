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

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.didelphis.utilities.Splitter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SplitterTest {

	private static final Map<String, String> DELIMITERS = new HashMap<>();

	static {
		DELIMITERS.put("[^", "]");
		DELIMITERS.put("(?:", ")");
		DELIMITERS.put("[", "]");
		DELIMITERS.put("(", ")");
		DELIMITERS.put("{", "}");
	}

	private static final Set<String> EMPTY_SET = Collections.emptySet();

	@Test
	void testWhitespace01() {
		String string = "a b c";

		assertEquals(asList("a", "b", "c"), whitespace(string, DELIMITERS));
	}

	@Test
	void testWhitespace02() {
		String string = "a     b  c";

		assertEquals(asList("a", "b", "c"), whitespace(string, DELIMITERS));
	}

	@Test
	void testWhitespaceWithBraces01() {
		String string = "a [b c]";

		List<String> expected = asList("a", "[b c]");
		List<String> received = whitespace(string, DELIMITERS);
		assertEquals(expected, received);
	}

	@Test
	void testWhitespaceWithBraces02() {
		String string = "a [b   c]  d";

		List<String> expected = asList("a", "[b   c]", "d");
		List<String> received = whitespace(string, DELIMITERS);
		assertEquals(expected, received);
	}

	@Test
	void testWhitespaceWithMultipleBraces01() {
		String string = "a [b   c]  d [e f]";

		List<String> expected = asList("a", "[b   c]", "d", "[e f]");
		List<String> received = whitespace(string, DELIMITERS);
		assertEquals(expected, received);
	}

	@Test
	void testWhitespaceWithMultipleBraces02() {
		String string = "a [b   c]  [d [e f]]";

		List<String> expected = asList("a", "[b   c]", "[d [e f]]");
		List<String> received = whitespace(string, DELIMITERS);
		assertEquals(expected, received);
	}

	@Test
	void testWhiteSpaceWithMultipleBraces03() {
		String string = "ab* (cd?)+ ((ae)*f)+";

		List<String> expected = asList("ab*", "(cd?)+", "((ae)*f)+");
		List<String> received = whitespace(string, DELIMITERS);
		assertEquals(expected, received);
	}

	@Test
	void testWhiteSpaceWithSets01() {
		List<String> list = toList("{ab* (cd?)+ ((ae)*f)+}tr",
				DELIMITERS,
				EMPTY_SET
		);
		assertEquals(3, list.size());
	}

	@Test
	void testWhiteSpaceWithSets02() {
		List<String> list = toList("{ab {cd xy} ef}tr", DELIMITERS, EMPTY_SET);
		assertEquals(3, list.size());
	}

	@Test
	void testParseParens01() {
		assertEquals(5, parseParens("[b c]", DELIMITERS, new HashSet<>(), 0));
	}

	@Test
	void testParseParens02() {
		assertEquals(7, parseParens("a [b c]", DELIMITERS, new HashSet<>(), 2));
	}

	@Test
	void testParseParens03() {
		assertEquals(11, parseParens("a [^bc[xy]]", DELIMITERS,
				new HashSet<>(),
				2));
	}
	
	@Test
	void testParseParens04() {
		String string = "[-con, +voice, -creaky][-son, -voice, +vot]us";
		assertEquals(23, parseParens(string, DELIMITERS, new HashSet<>(), 0));
	}
	
	@Test
	void testFindClosingBracketA01() {
		assertEquals(7, findClosingBracket("a [b c]", "[", DELIMITERS,
				new HashSet<>(),
				2));
	}

	@Test
	void testFindClosingBracketB01() {
		assertEquals(7, findClosingBracket("a [b c]", "[", DELIMITERS,
				new HashSet<>(),
				2));
	}

	@Test
	void testFindNoClosingBracket1() {
		assertEquals(-1, findClosingBracket("a [b c", "[", DELIMITERS,
				new HashSet<>(),
				2));
	}

	@Test
	void testFindNoClosingBracket2() {
		assertEquals(-1, findClosingBracket("a [b c[", "[", DELIMITERS,
				new HashSet<>(),
				2));
	}

	@Test
	void testFindNoClosingBracket3() {
		assertEquals(-1, findClosingBracket("a [b c [c]", "[", DELIMITERS,
				new HashSet<>(),
				2));
	}
	
	@Test
	void testEmbedded01() {
		List<String> specials = asList(
				"\\d",
				"\\D",
				"\\w",
				"\\W",
				"\\s",
				"\\S",
				"\\a",
				"\\A"
		);
		List<String> strings = toList(
				"([^'\"]+)",
				DELIMITERS,
				specials
		);

		List<String> expected = Collections.singletonList("([^'\"]+)");
		assertEquals(expected, strings);
	}
	
	@Test
	void testEmbedded02() {
		List<String> specials = asList(
				"\\d",
				"\\D",
				"\\w",
				"\\W",
				"\\s",
				"\\S",
				"\\a",
				"\\A"
		);
		List<String> strings = toList(
				"(.*[\\\\/])?[^\\\\/]+$",
				DELIMITERS,
				specials
		);

		List<String> expected = asList("(.*[\\\\/])","?","[^\\\\/]","+","$");
		assertEquals(expected, strings);
	}

	@Test
	void testEscapedBrackets() {
		List<String> specials = asList(
				"\\]",
				"\\["
		);
		
		String string = "\\[([^\\]]*)\\]";
		List<String> strings = toList(
				string,
				DELIMITERS,
				specials
		);
		
		List<String> expected = asList("\\[","([^\\]]*)","\\]");
		assertEquals(expected, strings);
	}

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
