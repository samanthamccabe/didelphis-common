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

import lombok.NonNull;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.statemachines.StateMachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class {@code StateMachineTestBase}
 *
 */
abstract class StateMachineTestBase<S> {
	
	void assertMatches(Automaton<S> machine, String input) {
		S target = transform(input);
		assertTrue(
				machine.matches(target),
				"Machine failed to accept an input it should have: " + target
		);
	}
	
	void assertNotMatches(Automaton<S> machine, String input) {
		S target = transform(input);
		assertFalse(
				machine.matches(target),
				"Machine accepted input it should not have: " + target
		);
	}

	void assertMatches(StateMachine<S> machine, String target, int start) {
		assertTrue(
				test(machine, target, start) >= 0,
				"Machine failed to accept an input it should have: " + target
		);
	}

	void assertNotMatches(StateMachine<S> machine, String input, int start) {
		assertFalse(
				test(machine, input, start)  >= 0,
				"Machine accepted input it should not have: " + input
		);
	}

	void assertMatchesGroup(
			@NonNull StateMachine<S> machine,
			@NonNull String input, 
			@NonNull String expected, 
			int group
	) {
		S target = transform(input);
		S exp = transform(expected);

		Match<S> match = machine.match(target, 0);
		S matchedGroup = match.group(group);
		assertEquals(exp, matchedGroup);
	}

	void assertNoGroup(
			@NonNull StateMachine<S> machine,
			@NonNull String input,
			int group
	) {
		S target = transform(input);
		Match<S> match = machine.match(target, 0);
		assertNull(match.group(group));
	}

	int test(StateMachine<S> machine, String input) {
		return test(machine, input, 0);
	}

	int test(StateMachine<S> machine, String input, int start) {
		S target = transform(input);
		Match<S> match = machine.match(target, start);
		return match.end();
	}

	@SafeVarargs
	final void assertMatch(
			StateMachine<S> machine, S input, int groupCount, S... groups
	) {
		Match<S> match = machine.match(input);

		assertTrue(match.matches());
		assertEquals(groupCount, match.groupCount());
		for (int i = 0; i < groups.length; i++) {
			S expected = groups[i];
			S received = match.group(i);
			String message = "Group " + i + " was expected to match "
					+ expected + " but actually matched " + received;
			assertEquals(expected, received, message);
		}
	}

	protected abstract S transform(String input);
}
