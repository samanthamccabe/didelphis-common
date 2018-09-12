package org.didelphis.language.automata.parsing;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.parsing.ParseException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class {@code RegexParserTest}
 *
 * @author Samantha Fiona McCabe
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

		assertTrue(expression.hasChildren());
		assertTrue(expression.isParallel());
		assertFalse(expression.isNegative());
		assertFalse(expression.isCapturing());
		assertEquals(3, expression.getChildren().size());
	}

	@Test
	void testParseSquareBracketsNegative() {

		String string = "[^abc]";

		Expression expression = PARSER.parseExpression(string);

		assertTrue(expression.hasChildren());
		assertTrue(expression.isParallel());
		assertTrue(expression.isNegative());
		assertEquals(3, expression.getChildren().size());
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
		assertTrue(expression.hasChildren());
		assertTrue(expression.isParallel());
		assertEquals(26, expression.getChildren().size());
	}

	@Test
	void testSquareBracketInvalidRange01() {
		String string = "[a-]";
		Expression expression = PARSER.parseExpression(string);
		assertTrue(expression.hasChildren());
		assertTrue(expression.isParallel());
		assertEquals(2, expression.getChildren().size());
	}

	@Test
	void testSquareBracketInvalidRange02() {
		String string = "[a-\\z]";
		assertThrowsParse(string);
	}
	
	@Test
	void testWordMeta() {
		String string = "\\w";
		Expression expression = PARSER.parseExpression(string);
		assertTrue(expression.hasChildren());
		assertTrue(expression.isParallel());
		assertEquals(63, expression.getChildren().size());
	}

	@Test
	void testGroupedMeta() {
		String string = "[\\d\\a]";
		Expression expression = PARSER.parseExpression(string);
		assertTrue(expression.hasChildren());
		assertTrue(expression.isParallel());
		List<Expression> children = expression.getChildren();
		assertEquals(2, children.size());

		Expression expression1 = children.get(0);
		assertTrue(expression1.isParallel());
		assertTrue(expression1.hasChildren());
		assertEquals(10, expression1.getChildren().size());
		
		Expression expression2 = children.get(1);
		assertTrue(expression2.isParallel());
		assertTrue(expression2.hasChildren());
		assertEquals(52, expression2.getChildren().size());

	}

	private static void assertThrowsParse(String expression) {
		assertThrows(
				ParseException.class,
				() -> PARSER.parseExpression(expression)
		);
	}
}
