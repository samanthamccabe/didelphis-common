package org.didelphis.language.automata.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.didelphis.language.automata.Automaton;
import org.didelphis.language.automata.JavaPatternAutomaton;
import org.intellij.lang.annotations.Language;

/**
 * Utility Class {@code Regex}
 * 
 * 
 * 
 * @author Samantha Fiona McCabe 
 * 9/14/2018
 */
@UtilityClass
public class Regex {

	@NonNull
	public Automaton<String> create(
			@NonNull @Language ("RegExp") String pattern,
			int flags
	) {
		return new JavaPatternAutomaton(pattern, flags);
	}

	@NonNull
	public Automaton<String> create(@NonNull @Language ("RegExp") String pattern) {
		return new JavaPatternAutomaton(pattern);
	}
}
