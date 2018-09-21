package org.didelphis.language.automata.parsing;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.expressions.ParallelNode;
import org.didelphis.language.automata.expressions.ParentNode;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Splitter;
import org.didelphis.utilities.Templates;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.didelphis.language.automata.parsing.LanguageParser.*;

/**
 * Class {@code RegexParser}
 *
 * A regular expression parser for creating expression trees that can be used to
 * construct a state machine. This is not intended to fully mimic the capability
 * of {@link java.util.regex.Pattern}; it is primarily intended to support a 
 * limited set of basic functions:
 * <ul>
 *     <li>Greedy quantification only</li>
 *     <li>Capturing and non-capturing groups</li>
 *     <li>Basic character classes</li>
 *     <li>Negations</li>
 *     <li>Simple character ranges</li>
 * </ul>
 * 
 * @author Samantha Fiona McCabe
 * @since 0.3.0
 */
@ToString
@EqualsAndHashCode
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
	}

	static Map<String, String> CLASSES = new HashMap<>();
	static {
		CLASSES.put("\\d", "[0-9]");
		CLASSES.put("\\D", "[^0-9]");
		CLASSES.put("\\w", "[a-zA-Z0-9_]");
		CLASSES.put("\\W", "[^a-zA-Z0-9_]");
		CLASSES.put("\\s", "[ \t\f\r\n]");
		CLASSES.put("\\S", "[^ \t\f\r\n]");
		CLASSES.put("\\a", "[a-zA-Z]"); // custom
		CLASSES.put("\\A", "[^a-zA-Z]"); // custom
	}

	static Set<String> ESCAPES = new HashSet<>();
	static {
		ESCAPES.add("\\$");
		ESCAPES.add("\\\\");
		ESCAPES.add("\\|");
		ESCAPES.add("\\[");
		ESCAPES.add("\\]");
		ESCAPES.add("\\(");
		ESCAPES.add("\\)");
		ESCAPES.add("\\.");
		ESCAPES.add("\\?");
		ESCAPES.add("\\*");
		ESCAPES.add("\\+");
		ESCAPES.add("\\-");
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
		validate(expression);
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

	/**
	 * Checks for the presence of illegal sub-patterns in the expression which
	 * are unambiguous errors.
	 *
	 * @param string an expression to be checked for basic errors
	 *
	 * @throws ParseException if basic structural errors are found in the
	 * 		expression. Such errors include:
	 * 		<ul>
	 * 		<li>Expression consisting only of a word-start {@code ^} or -stop
	 * 		{@code $} symbol</li>
	 * 		<li>Multiple quantification ({@code *?}, {@code ?+}</li>
	 * 		<li>Quantification of a boundary {@code ^?}</li>
	 * 		</ul>
	 */
	private static void validate(@NonNull String string) {
		
		if (string.equals("^") || string.equals("$")) {
			String template = Templates.create()
					.add("An expression must not consist of only a",
							" word-boundary {}")
					.with(string)
					.build();
			throw new ParseException(template);
		}
		
		for (int i = 0; i < string.length() - 1; i++) {
			String p = String.valueOf(string.charAt(i));
			String s = String.valueOf(string.charAt(i + 1));
			String message = null;
			
			if (QUANTIFIERS.contains(p) && QUANTIFIERS.contains(s)) {
				message = "Illegal multiple quantification {}{}";
			}
			if (p.equals("^") && QUANTIFIERS.contains(s)) {
				message = "Illegal modification of boundary {}{}";
			}
			if ((p.equals("$") && QUANTIFIERS.contains(s))) {
				message = "Illegal modification of boundary {}{}";
			}
			if (message != null) {
				String template = Templates.create()
						.add(message)
						.with(p, s)
						.data(string)
						.build();
				throw new ParseException(template);
			}
		}
	}

	private Expression parse(@NonNull List<String> split) {
		Buffer buffer = new Buffer();
		List<Expression> expressions = new ArrayList<>();
		
		boolean parallelParent = split.contains("|");
		
		for (String s : split) {
			
			//  the bar has been accounted for, so skip the character
			if (s.equals("|")) {
				continue;
			}
			
			if (QUANTIFIERS.contains(s)) {
				buffer.setQuantifier(s);
				buffer = update(buffer, expressions);
			} else if (startsWithDelimiter(s)) {
				buffer = update(buffer, expressions);
				if (s.length() <= 1) {
					String message = Templates.create()
							.add("Unmatched group delimiter {}")
							.with(s)
							.build();
					throw new ParseException(message);
				}
				int endIndex = s.length() - 1;
				if (s.startsWith("[^")) {
					String substring = s.substring(2, endIndex).trim();
					buffer.setNodes(parseRanges(substring));
					buffer.setParallel(true);
					buffer.setNegative(true);
				} else if (s.startsWith("[")) {
					String substring = s.substring(1, endIndex).trim();
					buffer.setNodes(parseRanges(substring));
					buffer.setParallel(true);
				} else if (s.startsWith("(?:")) {
					String substring = s.substring(3, endIndex).trim();
					List<String> list = split(substring);
					Expression exp = parse(list);
					List<Expression> exps = getChildrenOrExpression(exp);
					buffer.setNodes(exps);
				} else {
					String substring = s.substring(1, endIndex).trim();
					List<String> list = split(substring);
					Expression exp = parse(list);
					List<Expression> exps = getChildrenOrExpression(exp);
					buffer.setNodes(exps);
					buffer.setCapturing(true);
				}
			} else {
				buffer = update(buffer, expressions);
				buffer.setTerminal(s);
			}
		}
		update(buffer, expressions);
		if (expressions.size() == 1) {
			return expressions.get(0);
		} else if (parallelParent) {
			return new ParallelNode(expressions);
		} else {
			return new ParentNode(expressions);
		}
	}

	@NonNull
	private List<Expression> parseRanges(String string) {
		List<String> list1 = split(string);
		List<String> list2 = new ArrayList<>();
		for (int i = 0; i < list1.size();) {
			String s1 = list1.get(i);
			// check if there's an end range past the hyphen
			if (i + 2 < list1.size()) {
				if (list1.get(i + 1).equals("-")) {
					String s2 = list1.get(i + 2);
					if (s1.length() == 1 && s2.length() == 1) {
						char c1 = s1.charAt(0);
						char c2 = s2.charAt(0);
						if (c1 > c2) {
							String message = Templates.create()
									.add("Unable to parse expression {}")
									.with(string)
									.add("Start {} is greater than end {}")
									.with(c1, c2)
									.build();
							throw new ParseException(message);
						}
						
						list2.add(String.valueOf(c1));
						while (c1 < c2) {
							c1++;
							list2.add(String.valueOf(c1));
						}
						i+=2;
					} else {
						String message = Templates.create()
								.add("Unable to parse expression {}")
								.with(string)
								.add("due to invalid range {}-{}")
								.with(s1, s2)
								.build();
						throw new ParseException(message);
					}
				} else {
					list2.add(s1);
				}
			} else {
				list2.add(s1);
			}
			i++;
		}
		// will return a single non-parallel expression, so we get
		// its children and re-assign them to the current expression
		List<Expression> parsedChildren = parse(list2).getChildren();
		return new ArrayList<>(parsedChildren);
	}

	private boolean startsWithDelimiter(String s) {
		for (String delimiter : supportedDelimiters().keySet()) {
			if (s.startsWith(delimiter)) return true;
		}
		return false;
	}
	
	@Override
	public @Nullable String epsilon() {
		return "";
	}

	@NonNull
	@Override
	public MultiMap<String, String> getSpecialsMap() {
		return GeneralMultiMap.emptyMultiMap();
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
	private static List<String> split(String string) {
		List<String> list = Splitter.toList(string, DELIMITERS, CLASSES.keySet());
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			if (CLASSES.containsKey(s)) {
				list.set(i, CLASSES.get(s));
			}
		}
		return list;
	}

	@NonNull
	@Override
	public String subSequence(String sequence, int start, int end) {
		return sequence.substring(start, end);
	}
}
