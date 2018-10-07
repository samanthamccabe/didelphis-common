package org.didelphis.language.automata.matching;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.language.automata.parsing.StringParser;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.structures.tuples.Tuple;

import java.util.Collection;

/**
 * Class {@code RegexMatcher}
 *
 * @author Samantha Fiona McCabe
 */
@ToString
@EqualsAndHashCode
public class StringMatcher implements LanguageMatcher<String> {
	
	private final StringParser parser;
	private final MultiMap<String, String> specials;
	
	public StringMatcher(StringParser parser) {
		this.parser = parser;

		specials = new GeneralMultiMap<>();

		for (Tuple<String, Collection<String>> tuple : parser.getSpecialsMap()) {
			String key = parser.transform(tuple.getLeft());
			Collection<String> collection = tuple.getRight();
			specials.put(key, collection);
		}
	}
	
	@Override
	public int matches(
			@NonNull String input, @NonNull String arc, int index
	) {
		String subsequence = input.substring(index);
		if (specials.containsKey(arc)) {
			return specials.get(arc)
					.stream()
					.filter(value -> subsequence.startsWith(value))
					.findFirst()
					.map(String::length)
					.orElse(-1);
		}
		return subsequence.startsWith(arc) ? arc.length() : -1;
	}
}
