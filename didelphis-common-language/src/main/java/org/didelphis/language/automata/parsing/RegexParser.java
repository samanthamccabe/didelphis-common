package org.didelphis.language.automata.parsing;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.expressions.ParentNode;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Splitter;
import org.didelphis.utilities.Templates;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
 * @since 0.3.0
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
		SPECIALS.addAll("\\d", Arrays.asList("0","1","2","3","4","5","6","7","8","9"));
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
		Expression exp;
		try {
			exp = parse(split(expression));
			if (direction == ParseDirection.BACKWARD) {
				exp = exp.reverse();
			}
		} catch (ParseException e) {
			String message = Templates.create()
					.add("Failed to parse expression {}")
					.with(expression)
					.build();
			throw new ParseException(message, e);
		}
		return Expression.rewriteIds(exp, "0");
	}

	private Expression parse(@NonNull List<String> split) {
		Buffer buffer = new Buffer();
		List<Expression> expressions = new ArrayList<>();
		
		if (split.contains("|")) {
			buffer.setParallel(true);
		}
		
		for (String s : split) {
			
			//  the bar has been accounted for, so skip the character
			if (s.equals("|")) {
				continue;
			}
			
			if (QUANTIFIERS.contains(s)) {
				buffer.setQuantifier(s);
				buffer = update(buffer, expressions);
			} else if (DELIMITERS.containsKey(s.substring(0, 1))) {
				buffer = update(buffer, expressions);
				if (s.length() <= 1) {
					String message = Templates.create()
							.add("Unmatched group delimiter {}")
							.with(s)
							.build();
					throw new ParseException(message);
				}
				String substring = s.substring(1, s.length() - 1).trim();
				String delimiter = s.substring(0, 1);
				if (delimiter.equals("[")) {
					List<String> elements = Splitter.whitespace(substring, DELIMITERS);
					List<Expression> children = new ArrayList<>();
					for (String element : elements) {
						List<String> list = split(element);
						Expression parse = parse(list);
						children.add(parse);
					}
					buffer.setNodes(children);
					buffer.setParallel(true);
				} else if (delimiter.equals("(?:")) {
					List<String> list = split(substring);
					Expression exp = parse(list);
					buffer.setNodes(exp.getChildren());
				} else {
					List<String> list = split(substring);
					Expression exp = parse(list);
					buffer.setNodes(exp.getChildren());
					buffer.setCapturing(true);
				}
			} else {
				buffer = update(buffer, expressions);
				buffer.setTerminal(s);
			}
		}
		update(buffer, expressions);
		return expressions.size() == 1
				? expressions.get(0)
				: new ParentNode(expressions);
	}

	@NonNull
	private static Buffer update(
			@NonNull Buffer buffer,
			@NonNull Collection<Expression> children
	) {
		if (!buffer.isEmpty()) {
			Expression ex = buffer.toExpression();
			if (ex == null) {
				String message = Templates.create()
						.add("Unable to convert buffer to expression")
						.data(buffer)
						.build();
				throw new ParseException(message);
			}
			children.add(ex);
			return new Buffer();
		} else {
			return buffer;
		}
	}
	
	@Override
	public @Nullable String epsilon() {
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
		return Splitter.toList(substring, DELIMITERS, SPECIALS.keys());

	}

	@NonNull
	@Override
	public String subSequence(String sequence, int start, int end) {
		return sequence.substring(start, end);
	}
}
