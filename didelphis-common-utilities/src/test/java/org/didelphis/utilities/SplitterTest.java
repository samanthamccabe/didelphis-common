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

package org.didelphis.utilities;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SplitterTest {

	public static final Set<String> EMPTY_SET = Collections.emptySet();

	@Test
	void testWhitespace01() {
		String string = "a b c";

		assertEquals(Arrays.asList("a", "b", "c"), Splitter.whitespace(string));
	}

	@Test
	void testWhitespace02() {
		String string = "a     b  c";

		assertEquals(Arrays.asList("a", "b", "c"), Splitter.whitespace(string));
	}

	@Test
	void testWhitespaceWithBraces01() {
		String string = "a [b c]";

		List<String> expected = Arrays.asList("a", "[b c]");
		List<String> received = Splitter.whitespace(string);
		assertEquals(expected, received);
	}

	@Test
	void testWhitespaceWithBraces02() {
		String string = "a [b   c]  d";

		List<String> expected = Arrays.asList("a", "[b   c]", "d");
		List<String> received = Splitter.whitespace(string);
		assertEquals(expected, received);
	}

	@Test
	void testWhitespaceWithMultipleBraces01() {
		String string = "a [b   c]  d [e f]";

		List<String> expected = Arrays.asList("a", "[b   c]", "d", "[e f]");
		List<String> received = Splitter.whitespace(string);
		assertEquals(expected, received);
	}

	@Test
	void testWhitespaceWithMultipleBraces02() {
		String string = "a [b   c]  [d [e f]]";

		List<String> expected = Arrays.asList("a", "[b   c]", "[d [e f]]");
		List<String> received = Splitter.whitespace(string);
		assertEquals(expected, received);
	}
	
	@Test
	void testWhiteSpaceWithMultipleBraces03() {
		String string = "ab* (cd?)+ ((ae)*f)+";

		List<String> expected = Arrays.asList("ab*", "(cd?)+", "((ae)*f)+");
		List<String> received = Splitter.whitespace(string);
		assertEquals(expected, received);
	}
	
	@Test
	void testWhiteSpaceWithSets01() {
		List<String> list = Splitter.toList("{ab* (cd?)+ ((ae)*f)+}tr", EMPTY_SET);
		assertEquals(3, list.size());
	}

	@Test
	void testWhiteSpaceWithSets02() {
		List<String> list = Splitter.toList("{ab {cd xy} ef}tr", EMPTY_SET);
		assertEquals(3, list.size());
	}
	
	@Test
	void testParseParens01() {
		assertEquals(7, Splitter.parseParens("a [b c]", 2));
	}

	@Test
	void testFindClosingBracketA01() {
		assertEquals(7, Splitter.findClosingBracket("a [b c]", 2, '[', ']'));
	}

	@Test
	void testFindClosingBracketB01() {
		assertEquals(7, Splitter.findClosingBracket("a [b c]", "[", "]", 2));
	}
	
	@Test
	void testLineBreak01() {
		String string = "a\nb\nc\nd";
		List<String> expected = Arrays.asList("a", "b", "c", "d");
		assertEquals(expected, Splitter.lines(string));
	}

	@Test
	void testLineBreak02() {
		String string = "\na\nb\nc\nd\n";
		List<String> expected = Arrays.asList("","a", "b", "c", "d", "");
		assertEquals(expected, Splitter.lines(string));
	}

	@Test
	void testLineBreak03() {
		String string = "\na\n\nb\nc\nd\n";
		List<String> expected = Arrays.asList("", "a", "", "b", "c", "d", "");
		assertEquals(expected, Splitter.lines(string));
	}

	@Test
	void testLineBreak04() {
		String string = "\na\r\nb\nc\nd\n";
		List<String> expected = Arrays.asList("", "a", "b", "c", "d", "");
		assertEquals(expected, Splitter.lines(string));
	}

	@Test
	void testLineBreak05() {
		String string = "\na\r\nb\nc\r\rd\n";
		List<String> expected = Arrays.asList("", "a", "b", "c", "", "d", "");
		assertEquals(expected, Splitter.lines(string));
	}

	@Test
	void testLineBreak06() {
		String string = "";
		List<String> expected = Collections.singletonList("");
		assertEquals(expected, Splitter.lines(string));
	}

	@Test
	void testLineBreak07() {
		String string = "\n\r\n\n";
		List<String> expected = Arrays.asList("", "", "", "");
		assertEquals(expected, Splitter.lines(string));
	}
}
