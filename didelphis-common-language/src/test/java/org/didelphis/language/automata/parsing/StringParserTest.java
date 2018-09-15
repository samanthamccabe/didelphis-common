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

package org.didelphis.language.automata.parsing;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringParserTest {

	private final StringParser parser = new StringParser();

	@Test
	void testNegatedDot01() {
		assertThrowsParse("!.");
	}
	
	@Test
	void testIllegalBoundary01() {
		assertThrowsParse("a#?");
	}

	@Test
	void testIllegalBoundary02() {
		assertThrowsParse("a#+");
	}

	@Test
	void testIllegalBoundary03() {
		assertThrowsParse("a#*");
	}

	@Test
	void testIllegalBoundary04() {
		assertThrowsParse("#*a");
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
	void testUnmatchedCurly() {
		assertThrowsParse("{a");
	}

	@Test
	void testUnmatchedParen() {
		assertThrowsParse("(a");
	}
	
	@Test
	void tesTransform01() {
		assertEquals("a", parser.transform("a"));
		assertEquals("b", parser.transform("b"));
		assertEquals("c", parser.transform("c"));
		assertEquals("d", parser.transform("d"));
	}
	
	@Test
	void testBasic01() {
		Expression ex1 = parser.parseExpression("a");
		assertEquals("a", ex1.getTerminal());
		assertEquals("", ex1.getQuantifier());
		assertFalse(ex1.isNegative());
	}

	@Test
	void testBasic02() {
		Expression ex2 = parser.parseExpression("a?");
		assertEquals("a", ex2.getTerminal());
		assertEquals("?", ex2.getQuantifier());
		assertFalse(ex2.isNegative());
	}

	@Test
	void testBasic03() {
		Expression ex3 = parser.parseExpression("!a*");
		assertEquals("a", ex3.getTerminal());
		assertEquals("*", ex3.getQuantifier());
		assertTrue(ex3.isNegative());
	}

	@Test
	void testBasic04() {
		Expression ex3 = parser.parseExpression("!(ab)*");
		assertEquals(2, ex3.getChildren().size());
		assertEquals("*", ex3.getQuantifier());
		assertTrue(ex3.isNegative());
	}

	@Test
	void testBasic05() {
		Expression ex3 = parser.parseExpression("(a!(xy)?b)+");
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
		assertTrue(ch1.isNegative());
	}
	
	@Test
	void testNegationGroups() {
		Expression expression = parser.parseExpression("!((ab)+)xy#");
		List<Expression> children = expression.getChildren();
		assertEquals(4, children.size());
		Expression first = children.get(0);
		assertTrue(first.isNegative());
	}
	
	@Test
	void testReverse() {
		Expression ex1 = parser.parseExpression("(a!(xy)?b)+");
		Expression ex2 = parser.parseExpression("(b!(yx)?a)+");
		Expression rev1 = Expression.rewriteIds(ex1.reverse(), "0");
		Expression rev2 = Expression.rewriteIds(ex2.reverse(), "0");
		assertEquals(ex1, rev2);
		assertEquals(rev1, ex2);
	}

	@Test
	void testReverse01() {
		Expression ex1 = parser.parseExpression("(a!(xy)?b)+");
		assertEquals(ex1.reverse(), ex1.reverse());
		assertEquals(ex1, ex1.reverse().reverse());
	}
	
	@Test
	void testToString01() {
		Expression ex1 = parser.parseExpression("(a!(xy)?b)+");
		assertEquals("(a!(xy)?b)+", ex1.toString());
	}
	
	@Test
	void testComplex01() {
		Expression ex = parser.parseExpression("{ab* (cd?)+ ((ae)*f)+}tr");
		List<Expression> children = ex.getChildren();
		assertEquals(3, children.size());
		Expression child1 = children.get(0);
		assertEquals(3, child1.getChildren().size());
		assertEquals(2, child1.getChildren().get(0).getChildren().size());
	}

	@Test
	void testComplex01Reverse() {
		Expression ex1 = parser.parseExpression("{ab* (cd?)+ ((ae)*f)+}tr");
		Expression ex2 = parser.parseExpression("rt{b*a (d?c)+ (f(ea)*)+}");
		Expression rev1 = Expression.rewriteIds(ex2.reverse(), "0");
		Expression rev2 = Expression.rewriteIds(ex1.reverse(),"0");

		assertEquals(ex1, rev1);
		assertEquals(rev2, ex2);
	}

	@Test
	void testNestedGroups01() {
		String string = "a(b)";
		Expression expression = parser.parseExpression(string);
		assertTrue(expression.hasChildren());
		assertEquals(2, expression.getChildren().size());
	}
	
	@Test
	void testNestedGroups02() {
		String string = "(a(b))";
		Expression expression = parser.parseExpression(string);
		assertTrue(expression.hasChildren());
		assertEquals(2, expression.getChildren().size());
	}

	@Test
	void testSetWithBoundary01() {
		Expression ex = parser.parseExpression("{x #}a");
		List<Expression> children = ex.getChildren();
		assertEquals(2, children.size());
		Expression child1 = children.get(0);
		List<Expression> child2 = child1.getChildren();
		assertEquals(2, child2.size());

		assertEquals("#[", child2.get(1).getTerminal());
	}
	
	@Test
	void testSetWithBoundary02() {
		Expression ex = parser.parseExpression("a{x #}");
		List<Expression> children = ex.getChildren();
		assertEquals(2, children.size());
		Expression child1 = children.get(1);
		List<Expression> child2 = child1.getChildren();
		assertEquals(2, child2.size());
		assertEquals("]#", child2.get(1).getTerminal());
	}

	@Test
	void testSpecials01() {
		MultiMap<String, String> specials = new GeneralMultiMap<>();
		specials.add("CH",  "th");
		Expression ex1 = new StringParser(specials).parseExpression("ataCHam");
		assertTrue(ex1.hasChildren());
		List<Expression> children = ex1.getChildren();
		assertEquals("a",  children.get(0).getTerminal());
		assertEquals("t",  children.get(1).getTerminal());
		assertEquals("a",  children.get(2).getTerminal());
		assertEquals("CH", children.get(3).getTerminal());
		assertEquals("a",  children.get(4).getTerminal());
		assertEquals("m",  children.get(5).getTerminal());
	}

	private void assertThrowsParse(String expression) {
		assertThrows(
				ParseException.class,
				() -> parser.parseExpression(expression)
		);
	}
}
