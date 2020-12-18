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
import org.didelphis.language.automata.matching.BasicMatch;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.graph.Arc;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SequenceParserTest {

	private final SequenceParser parser;
	private final SequenceFactory factory;

	SequenceParserTest() {
		FeatureModelLoader loader = new FeatureModelLoader();
		factory = new SequenceFactory(
				loader.getFeatureMapping(),
				FormatterMode.NONE
		);
		parser = new SequenceParser(factory);
	}

	@Nested
	@DisplayName("Conditions under which parsing should fail")
	class TestParseError {

		@Test
		void testIllegalNegation01() {
			assertThrowsParse("!!a");
		}

		@Test
		void testIllegalNegation02() {
			assertThrowsParse("!?a");
			assertThrowsParse("!*a");
			assertThrowsParse("!+a");
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

		private void assertThrowsParse(String expression) {
			assertThrows(
					ParseException.class,
					() -> parser.parseExpression(expression)
			);
		}
	}

	@Nested
	@DisplayName("Conditions under which parsing should succeed")
	class TestParse {
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
			MultiMap<String, Sequence> specials = new GeneralMultiMap<>();
			specials.add("CH",  parser.transform("th"));
			SequenceParser p = new SequenceParser(factory, specials);
			Expression ex1 = p.parseExpression("ataCHam");
			assertTrue(ex1.hasChildren());
			List<Expression> children = ex1.getChildren();
			assertEquals("a",  children.get(0).getTerminal());
			assertEquals("t",  children.get(1).getTerminal());
			assertEquals("a",  children.get(2).getTerminal());
			assertEquals("CH", children.get(3).getTerminal());
			assertEquals("a",  children.get(4).getTerminal());
			assertEquals("m",  children.get(5).getTerminal());
		}

		@Test
		void testReplaceGroups() {
			Sequence sequence = parser.transform("ao");
			BasicMatch<Sequence> match = new BasicMatch<>(sequence,0,2);
			match.addGroup(0, 2, sequence);
			match.addGroup(0, 1, parser.transform("a"));
			match.addGroup(1, 2, parser.transform("o"));
			Sequence input = parser.transform("$1x$2");
			Sequence replaced = parser.replaceGroups(input, match);
			Sequence expected = parser.transform("axo");
			assertEquals(expected, replaced);
		}
	}

	@Nested
	@DisplayName("Functioning of inner Arc classses")
	class TestArcs {

		@Test
		void testEpsilon() {
			Arc<Sequence> epsilon = parser.epsilon();
			assertEquals(0,epsilon.match(parser.transform("a"),0));
			assertEquals("", epsilon.toString());
		}

		@Test
		void testWordStart() {
			Arc<Sequence> arc = parser.getArc("#[");
			assertEquals("^", arc.toString());
			assertEquals(0,  arc.match(factory.toSequence("aa"), 0));
			assertEquals(-1, arc.match(factory.toSequence("aa"), 1));
		}

		@Test
		void testWordEnd() {
			Arc<Sequence> arc = parser.getArc("]#");
			assertEquals("$", arc.toString());
			assertEquals(-1, arc.match(factory.toSequence("aa"), 0));
			assertEquals( 2, arc.match(factory.toSequence("aa"), 2));
		}

		@Test
		void testDot() {
			Arc<Sequence> arc = parser.getDot();
			assertEquals(".", arc.toString());
			assertEquals( 1, arc.match(factory.toSequence("aa"), 0));
			assertEquals( 2, arc.match(factory.toSequence("aa"), 1));
			assertEquals(-1, arc.match(factory.toSequence("aa"), 2));
		}
	}

}
