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

package org.didelphis.language.automata.parsing;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class {@code RegexParserTest}
 *
 * @since 0.3.0
 */
class RegexParserTest {

	private static final RegexParser PARSER = new RegexParser();

	@Test
	void testWordStartOnly() {
		assertThrowsParse("^");
	}

	@Test
	void testWordEndOnly() {
		assertThrowsParse("$");
	}

	@Test
	void testIllegalBoundary01() {
		assertThrowsParse("a$?");
	}

	@Test
	void testIllegalBoundary02() {
		assertThrowsParse("a$+");
	}

	@Test
	void testIllegalBoundary03() {
		assertThrowsParse("a$*");
	}

	@Test
	void testIllegalBoundary04() {
		assertThrowsParse("^*a");
	}

	@Test
	void testStarQuestionMark() {
		assertThrowsParse("a*?");
	}

	@Test
	void testPlusQuestionMark() {
		assertThrowsParse("a+?");
	}

	@Test
	void testPlusStarMark() {
		assertThrowsParse("a+*");
	}

	@Test
	void testUnmatchedParen() {
		assertThrowsParse("(a");
	}

	@Test
	void testUnmatchedSquare() {
		assertThrowsParse("[a");
	}

	@Test
	void testGetQuantifiers() {
		Set<String> set = PARSER.supportedQuantifiers();

		assertTrue(set.contains("?"));
		assertTrue(set.contains("*"));
		assertTrue(set.contains("+"));
		assertFalse(set.contains("!"));

		//noinspection ConstantConditions
		assertThrows(UnsupportedOperationException.class, () -> set.add(""));
	}

	@Test
	void testParse() {

		String string = "abc";

		Expression expression = PARSER.parseExpression(string);

		assertTrue(expression.hasChildren());
		assertFalse(expression.isParallel());
		assertFalse(expression.isNegative());
		assertFalse(expression.isCapturing());
		assertEquals(3, expression.getChildren().size());
	}

	@Test
	void testParseParallel() {

		String string = "a|b|c";

		Expression expression = PARSER.parseExpression(string);

		assertTrue(expression.hasChildren());
		assertTrue(expression.isParallel());
		assertFalse(expression.isNegative());
		assertFalse(expression.isCapturing());
		assertEquals(3, expression.getChildren().size());
	}

	@Test
	void testParseSquareBrackets() {

		String string = "[abc]";

		Expression expression = PARSER.parseExpression(string);
		assertFalse(expression.hasChildren());
	}

	@Test
	void testParseSquareBracketsNegative() {

		String string = "[^abc]";

		Expression expression = PARSER.parseExpression(string);
		assertFalse(expression.hasChildren());
	}

	@Test
	void testParseCapturing() {

		String string = "(abc)";

		Expression expression = PARSER.parseExpression(string);

		assertTrue(expression.hasChildren());
		assertFalse(expression.isParallel());
		assertFalse(expression.isNegative());
		assertTrue(expression.isCapturing());
		assertEquals(3, expression.getChildren().size());
	}

	@Test
	void testParseNonCapturing() {

		String string = "(?:abc)";

		Expression expression = PARSER.parseExpression(string);

		assertTrue(expression.hasChildren());
		assertFalse(expression.isParallel());
		assertFalse(expression.isNegative());
		assertFalse(expression.isCapturing());
		assertEquals(3, expression.getChildren().size());
	}

	@Test
	void testSquareBracketRange() {
		String string = "[a-z]";
		Expression expression = PARSER.parseExpression(string);
		assertFalse(expression.hasChildren());
	}

	@Test
	void testWordMeta() {
		String string = "\\w";
		Expression expression = PARSER.parseExpression(string);
		assertFalse(expression.hasChildren());

	}

	@Test
	void testGroupedMeta() {
		String string = "[\\d\\a]";
		Expression expression = PARSER.parseExpression(string);
		assertFalse(expression.hasChildren());
	}

	@Test
	void testNestedGroups01() {
		String string = "(a(b))";
		Expression expression = PARSER.parseExpression(string);
		assertTrue(expression.hasChildren());
		assertEquals(2, expression.getChildren().size());
	}

	@Test
	void testNestedGroups02() {
		String string = "^(a(b)?)+$";
		Expression expression = PARSER.parseExpression(string);
		assertTrue(expression.hasChildren());
		assertEquals(3, expression.getChildren().size());
	}

	@Test
	void testNestedGroups03() {
		Expression ex3 = PARSER.parseExpression("(a(xy)?b)+");
		assertTrue(ex3.isCapturing());

		List<Expression> children = ex3.getChildren();
		assertEquals(3, children.size());
		assertEquals("+", ex3.getQuantifier());
		assertFalse(ex3.isNegative());

		assertEquals("a", children.get(0).getTerminal());
		assertEquals("b", children.get(2).getTerminal());

		assertFalse(children.get(0).isCapturing());
		assertFalse(children.get(2).isCapturing());

		Expression ch1 = children.get(1);
		assertTrue(ch1.isCapturing());
		assertEquals(2, ch1.getChildren().size());
		assertEquals("?", ch1.getQuantifier());
	}

	@Test
	void testReverse01() {
		Expression ex1 = PARSER.parseExpression("(a(xy)?b)+");
		Expression ex2 = PARSER.parseExpression("(b(yx)?a)+", ParseDirection.BACKWARD);
		Expression rev1 = Expression.rewriteIds(ex1, "0");
		Expression rev2 = Expression.rewriteIds(ex2, "0");
		assertEquals(ex1, rev2);
		assertEquals(rev1, ex2);
	}

	@Test
	void testReverse02() {
		Expression ex1 = PARSER.parseExpression("(a(xy)?b)+");
		Expression ex2 = PARSER.parseExpression("(b(yx)?a)+");
		Expression rev1 = Expression.rewriteIds(ex1.reverse(), "0");
		Expression rev2 = Expression.rewriteIds(ex2.reverse(), "0");
		assertEquals(ex1, rev2);
		assertEquals(rev1, ex2);
	}

	@Test
	void testParseEscapes() {
		Expression expression = PARSER.parseExpression("\\[([^\\]]*)\\]");

		assertEquals(3, expression.getChildren().size());
	}

	private static void assertThrowsParse(String expression) {
		assertThrows(
				ParseException.class,
				() -> PARSER.parseExpression(expression)
		);
	}
}
