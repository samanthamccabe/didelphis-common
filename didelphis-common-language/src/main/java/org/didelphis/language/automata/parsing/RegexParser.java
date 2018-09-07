package org.didelphis.language.automata.parsing;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class {@code RegexParser}
 *
 * @author Samantha Fiona McCabe
 * @date 9/5/18
 */
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegexParser implements LanguageParser<String> {

	static Map<String, String> DELIMITERS = new HashMap<>();
	static {
		DELIMITERS.put("[","]");
		DELIMITERS.put("[^","]");
		DELIMITERS.put("(",")");
		DELIMITERS.put("(?:",")");
	}
	
	static Set<String> QUANTIFIERS = new HashSet<>();
	static {
		QUANTIFIERS.add("?");
		QUANTIFIERS.add("*");
		QUANTIFIERS.add("+");
		// todo:
	}

	static MultiMap<String, String> SPECIALS = new GeneralMultiMap<>();
	static {
		SPECIALS.add("\\d", "[0-9]");
		//todo:
	}

	@NonNull
	@Override
	public Map<String, String> supportedDelimiters() {
		return Collections.unmodifiableMap(DELIMITERS);
	}

	@NonNull
	@Override
	public Set<String> supportedQuantifiers() {
		return Collections.unmodifiableSet(QUANTIFIERS);
	}

	@NonNull
	@Override
	public String getWordStart() {
		return "^";
	}

	@NonNull
	@Override
	public String getWordEnd() {
		return "$";
	}

	@NonNull
	@Override
	public String transform(String expression) {
		return expression;
	}

	@NonNull
	@Override
	public Expression parseExpression(
			@NonNull String expression, @NonNull ParseDirection direction
	) {
		// TODO: ---------------------------------------------------------------
		return null;
	}

	@Nullable
	@Override
	public String epsilon() {
		return "";
	}

	@NonNull
	@Override
	public MultiMap<String, String> getSpecials() {
		return SPECIALS;
	}

	@NonNull
	@Override
	public String getDot() {
		return ".";
	}

	@Override
	public int lengthOf(@NonNull String t) {
		return t.length();
	}
	
	@NonNull
	@Override
	public List<String> split(String substring) {
		// TODO: ---------------------------------------------------------------
		return null;
	}

	@NonNull
	@Override
	public String subSequence(String sequence, int start, int end) {
		return sequence.substring(start, end);
	}
}
