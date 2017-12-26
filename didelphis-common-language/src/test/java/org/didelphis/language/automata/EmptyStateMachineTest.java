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

import org.didelphis.language.automata.interfaces.MachineMatcher;
import org.didelphis.language.automata.interfaces.StateMachine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

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
		assertTrue(instance.getMatcher() instanceof MachineMatcher);
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
		assertEquals(Collections.singleton(0), instance.getMatchIndices(0, "foo"));
		assertEquals(Collections.singleton(1), instance.getMatchIndices(1, "foo"));
		assertEquals(Collections.singleton(2), instance.getMatchIndices(2, "foo"));
		assertEquals(Collections.singleton(3), instance.getMatchIndices(3, "foo"));
		assertEquals(Collections.singleton(5), instance.getMatchIndices(5, "foo"));
		assertEquals(Collections.singleton(8), instance.getMatchIndices(8, "foo"));


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
