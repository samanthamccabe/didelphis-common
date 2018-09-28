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

package org.didelphis.language.automata;

import org.didelphis.language.automata.matching.RegexMatcher;
import org.didelphis.language.automata.parsing.RegexParser;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.didelphis.language.automata.statemachines.StandardStateMachine.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmptyStateMachineTest {

	private static RegexParser parser;
	private static RegexMatcher matcher;
	private static StateMachine<String> instance;

	@BeforeAll
	static void init() {
		parser  = new RegexParser();
		matcher = new RegexMatcher();
		instance = create("_", "", parser, matcher);
	}

	@Test
	void getParser() {
		assertSame(parser, instance.getParser());
	}

	@Test
	void getMatcher() {
		assertSame(matcher, instance.getMatcher());
	}

	@Test
	void getId() {
		assertEquals("_", instance.getId());
	}

	@Test
	void getGraphs() {
		assertTrue(instance.getGraphs().isEmpty());
	}

	@Test
	void getMatchIndices() {
		assertEquals(0, instance.match("foo", 0).end());
		assertEquals(1, instance.match("foo", 1).end());
		assertEquals(2, instance.match("foo", 2).end());
	}

	@Test
	void equals() {
		assertEquals(create("_", "", parser, matcher), instance);
		assertNotEquals(create("_", ".", parser, matcher), instance);

	}

	@Test
	void testHashCode() {
		assertEquals(create("_", "", parser, matcher).hashCode(), instance.hashCode());
		assertNotEquals(create("_", ".", parser, matcher).hashCode(), instance.hashCode());
	}
}
