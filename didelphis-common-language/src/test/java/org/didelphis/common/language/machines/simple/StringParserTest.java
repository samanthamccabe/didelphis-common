package org.didelphis.common.language.machines.simple;

import org.didelphis.common.language.machines.Expression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by samantha on 3/3/17.
 */
class StringParserTest {
	
	private static final StringParser PARSER = StringParser.getInstance();
	
	@Test
	void parseExpression_Plain01() {
		List<Expression> list = PARSER.parseExpression("ab");
		assertEquals(2, list.size());
		Expression expression1 = list.get(0);
		assertEquals("a",expression1.getExpression());
		assertEquals("",expression1.getMetacharacter());

		Expression expression2 = list.get(1);
		assertEquals("b",expression2.getExpression());
		assertEquals("",expression2.getMetacharacter());
	}
	
	@Test
	void parseExpression_Star01() {
		List<Expression> list = PARSER.parseExpression("a*");
		assertEquals(1, list.size());
		Expression expression = list.get(0);
		assertEquals("a",expression.getExpression());
		assertEquals("*",expression.getMetacharacter());
	}

	@Test
	void parseExpression_Star02() {
		List<Expression> list = PARSER.parseExpression("a+");
		assertEquals(1, list.size());
		Expression expression = list.get(0);
		assertEquals("a",expression.getExpression());
		assertEquals("+",expression.getMetacharacter());
	}

	@Test
	void parseExpression_Star03() {
		List<Expression> list = PARSER.parseExpression("a?");
		assertEquals(1, list.size());
		Expression expression = list.get(0);
		assertEquals("a",expression.getExpression());
		assertEquals("?",expression.getMetacharacter());
	}
	
	@Test
	void parseExpression_Brackets01() {
		List<Expression> list = PARSER.parseExpression("(ab)");
		assertEquals(1, list.size());
		Expression expression = list.get(0);
		assertEquals("(ab)",expression.getExpression());
		assertEquals("",expression.getMetacharacter());
	}

	@Test
	void parseExpression_Brackets02() {
		List<Expression> list = PARSER.parseExpression("[ab]");
		assertEquals(1, list.size());
		Expression expression = list.get(0);
		assertEquals("[ab]",expression.getExpression());
		assertEquals("",expression.getMetacharacter());
	}

	@Test
	void parseExpression_BracketsStar01() {
		List<Expression> list = PARSER.parseExpression("(ab)+");
		assertEquals(1, list.size());
		Expression expression = list.get(0);
		assertEquals("(ab)",expression.getExpression());
		assertEquals("+",expression.getMetacharacter());
	}

	@Test
	void parseExpression_BracketsStar02() {
		List<Expression> list = PARSER.parseExpression("[ab]*");
		assertEquals(1, list.size());
		Expression expression = list.get(0);
		assertEquals("[ab]",expression.getExpression());
		assertEquals("*", expression.getMetacharacter());
	}

	@Test
	@DisplayName("Length 3 expression with square braces and *")
	void parseExpression_Long02() {
		List<Expression> list = PARSER.parseExpression("ac[ab]*");
		assertEquals(3, list.size());
		test(list.get(0), "a", "");
		test(list.get(1), "c", "");
		test(list.get(2), "[ab]", "*");
	}
	
	private static void test(Expression ex, String symb, String meta) {
		assertEquals(symb, ex.getExpression());
		assertEquals(meta, ex.getMetacharacter());
	}
}