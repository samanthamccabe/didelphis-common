package org.didelphis.language.automata.matching;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.language.automata.parsing.RegexParser;

/**
 * Class {@code RegexMatcher}
 *
 * @author Samantha Fiona McCabe
 */
@ToString
@EqualsAndHashCode
public class RegexMatcher implements LanguageMatcher<String> {
	
	private final RegexParser parser = new RegexParser();
	
	@Override
	public int matches(
			@NonNull String input, @NonNull String arc, int index
	) {
		// We were going to handle the escapes here, but those are not actually
		// used by the matcher (though they could be, and probably should be).
		// Currently (2018-09-21) all escapes and character classes are expanded
		// when the state machine is created, so only literal arcs are present
		// in the graph. Evaluating this is a potential source of slowdown, but
		// the performance has not been benchmarked
		return input.startsWith(arc, index) ? arc.length() : -1;
	}
}
