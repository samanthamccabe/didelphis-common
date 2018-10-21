package org.didelphis.language.automata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RegexTest {

	@Test
	void testInsensitive() {
		Automaton<String> machine = new Regex("\\s*or\\s*", true);
		assertTrue(machine.matches("  OR   "));
		assertTrue(machine.matches("OR   "));
		assertTrue(machine.matches(" or "));
	}
}