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

import org.didelphis.language.automata.matching.LanguageMatcher;
import org.didelphis.language.automata.matching.BasicMatch;
import org.didelphis.language.automata.statemachines.EmptyStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmptyStateMachineTest {

	private static StateMachine<String> instance;

	@BeforeAll
	static void init() {
		instance = EmptyStateMachine.getInstance();
	}

	@Test
	void getParser() {
		assertThrows(
				UnsupportedOperationException.class,
				() -> instance.getParser()
		);
	}

	@Test
	void getMatcher() {
		assertTrue(instance.getMatcher() instanceof LanguageMatcher);
	}

	@Test
	void getId() {
		assertEquals("Empty State Machine", instance.getId());
	}

	@Test
	void getGraphs() {
		assertTrue(instance.getGraphs().isEmpty());
	}

	@Test
	void getMatchIndices() {
		assertEquals(new BasicMatch<>("foo", 0, 0), instance.match("foo",0));
		assertEquals(new BasicMatch<>("foo", 0, 1), instance.match("foo", 1));
		assertEquals(new BasicMatch<>("foo", 0, 2), instance.match("foo", 2));
	}

	@Test
	void equals() {
		assertEquals(EmptyStateMachine.getInstance(), instance);
	}

	@Test
	void testHashCode() {
		assertEquals(EmptyStateMachine.getInstance().hashCode(), instance.hashCode());
	}
}
