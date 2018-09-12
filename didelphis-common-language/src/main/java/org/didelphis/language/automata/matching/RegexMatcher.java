package org.didelphis.language.automata.matching;

import lombok.NonNull;
import org.didelphis.language.automata.parsing.RegexParser;
import org.didelphis.structures.maps.interfaces.MultiMap;

import java.util.Collection;

/**
 * Class {@code RegexMatcher}
 *
 * @author Samantha Fiona McCabe
 */
public class RegexMatcher implements LanguageMatcher<String> {
	
	private final RegexParser parser = new RegexParser();
	
	@Override
	public int matches(
			@NonNull String input, @NonNull String arc, int index
	) {
		MultiMap<String, String> specialsMap = parser.getSpecialsMap();
		if (specialsMap.containsKey(arc)) {
			Collection<String> strings = specialsMap.get(arc);
			for (String string : strings) { 
				if (input.startsWith(string, index)) {
					return string.length();
				}
			}
		}
		return input.startsWith(arc, index) ? arc.length() : -1;
	}
}
