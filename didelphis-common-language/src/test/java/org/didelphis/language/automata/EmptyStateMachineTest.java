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

package org.didelphis.language.automata;

import org.didelphis.language.automata.parsing.RegexParser;
import org.didelphis.language.automata.statemachines.StateMachine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.didelphis.language.automata.statemachines.StandardStateMachine.*;
import static org.junit.jupiter.api.Assertions.*;

class EmptyStateMachineTest {

	private static RegexParser parser;
	private static StateMachine<String> instance;

	@BeforeAll
	static void init() {
		parser  = new RegexParser();
		instance = create("_", "", parser);
	}

	@Test
	void getParser() {
		assertSame(parser, instance.getParser());
	}

	@Test
	void getId() {
		assertEquals("_", instance.getId());
	}

	@Test
	void getMatchIndices() {
		assertEquals(0, instance.match("foo", 0).end());
		assertEquals(1, instance.match("foo", 1).end());
		assertEquals(2, instance.match("foo", 2).end());
	}

	@Test
	void equals() {
		assertEquals(create("_", "", parser), instance);
		assertNotEquals(create("_", ".", parser), instance);

	}

	@Test
	void testHashCode() {
		assertEquals(create("_", "", parser).hashCode(), instance.hashCode());
		assertNotEquals(create("_", ".", parser).hashCode(), instance.hashCode());
	}
}
