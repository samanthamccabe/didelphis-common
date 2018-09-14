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
 * @author Samantha Fiona McCabe
 * @date 9/14/2018
 */
abstract class StateMachineTestBase<S> {
	
	void assertMatches(StateMachine<S> machine, String target) {
		assertTrue(
				test(machine, target) >= 0,
				"Machine failed to accept an input it should have: " + target
		);
	}
	
	void assertNotMatches(StateMachine<S> machine, String input) {
		assertFalse(
				test(machine, input) >= 0,
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
		S target = transform(input);
		Match<S> match = machine.match(target, 0);
		return match.end();
	}

	protected abstract S transform(String input);
}
