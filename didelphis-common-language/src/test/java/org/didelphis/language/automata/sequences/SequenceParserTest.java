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

package org.didelphis.language.automata.sequences;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.BinaryFeature;
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SequenceParserTest {

	private SequenceParser<?> parser;
	private SequenceFactory<?> factory;
	private FeatureModelLoader<?> loader;

	@BeforeEach
	void init() {
		loader = new FeatureModelLoader<>(BinaryFeature.INSTANCE);
		factory = new SequenceFactory<>(
				loader.getFeatureMapping(),
				FormatterMode.NONE
		);
		parser = new SequenceParser<>(factory);
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
		List<Expression> children = ex3.getChildren();
		assertEquals(3, children.size());
		assertEquals("+", ex3.getQuantifier());
		assertFalse(ex3.isNegative());
		
		assertEquals("a", children.get(0).getTerminal());
		assertEquals("b", children.get(2).getTerminal());

		Expression ch1 = children.get(1);
		assertEquals(2, ch1.getChildren().size());
		assertEquals("?", ch1.getQuantifier());
		assertTrue(ch1.isNegative());
	}
	
	@Test
	void testReverse() {
		Expression ex1 = parser.parseExpression("(a!(xy)?b)+");
		Expression ex2 = parser.parseExpression("(b!(yx)?a)+");
		assertEquals(ex1, ex2.reverse());
		assertEquals(ex1.reverse(), ex2);
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
		assertEquals(ex1, ex2.reverse());
		assertEquals(ex1.reverse(), ex2);
	}
}
