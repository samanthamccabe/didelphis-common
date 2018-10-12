package org.didelphis.language.automata.parsing;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.expressions.ParallelNode;
import org.didelphis.language.automata.expressions.ParentNode;
import org.didelphis.language.automata.expressions.TerminalNode;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Splitter;
import org.didelphis.utilities.Templates;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.didelphis.language.automata.parsing.LanguageParser.getChildrenOrExpression;
import static org.didelphis.language.automata.parsing.LanguageParser.update;

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

	static Map<String, String> DELIMITERS_ALT = new HashMap<>();
	static Map<String, String> DELIMITERS     = new HashMap<>();
	static {
		DELIMITERS.put("[", "]");
		DELIMITERS.put("[^", "]");
		DELIMITERS.put("(", ")");
		DELIMITERS.put("(?:", ")");
		DELIMITERS_ALT.put("[", "]");
		DELIMITERS_ALT.put("[^", "]");
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
		
		CLASSES.put("[:alnum:]",  "[A-Za-z0-9]");
		CLASSES.put("[:alpha:]",  "[A-Za-z]");
		CLASSES.put("[:blank:]",  "[ \t]");
		CLASSES.put("[:digit:]",  "[0-9]");
		CLASSES.put("[:lower:]",  "[a-z]");
		CLASSES.put("[:upper:]",  "[A-Z]");
		CLASSES.put("[:xdigit:]", "[A-Fa-f0-9]");
		CLASSES.put("[:punct:]",  "[!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-\\[\\]]");
		CLASSES.put("[:space:]",  "[ \t\f\r\n\\v]");
	}

	static Map<String, String> ESCAPES = new HashMap<>();
	static {
		ESCAPES.put("\\$",  "$");
		ESCAPES.put("\\\\", "\\");
		ESCAPES.put("\\|",  "|");
		ESCAPES.put("\\[",  "[");
		ESCAPES.put("\\]",  "]");
		ESCAPES.put("\\(",  "(");
		ESCAPES.put("\\)",  ")");
		ESCAPES.put("\\.",  ".");
		ESCAPES.put("\\?",  "?");
		ESCAPES.put("\\*",  "*");
		ESCAPES.put("\\+",  "+");
		ESCAPES.put("\\-",  "-");
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
			@NonNull @Language ("RegExp") String expression, 
			@NonNull ParseDirection direction
	) {
		Expression exp;
		try {
			List<String> split = split(expression);
			validate(split);
			exp = parse(split);
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
	 * @param list an expression to be checked for basic errors
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
	private static void validate(@NonNull List<String> list) {
		
		if (list.size() == 1) {
			String string = list.get(0);
			if (string.equals("^") || string.equals("$")) {
				String template = Templates.create().add(
						"An expression must not consist of only a",
						" word-boundary {}"
				).with(list).build();
				throw new ParseException(template);
			}
			return;
		}
		
		for (int i = 0; i < list.size() - 1; i++) {
			String p = list.get(i);
			String s = list.get(i + 1);
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
						.data(list)
						.build();
				throw new ParseException(template);
			}
		}
	}

	private Expression parse(@NonNull List<String> split) {
		Buffer buffer = new Buffer();
		List<Expression> expressions = new ArrayList<>();
		
		if(split.contains("|")) {
			int cursor = 0;
			for (int i = 0; i < split.size(); i++) {
				if (split.get(i).equals("|")) {
					expressions.add(parse(split.subList(cursor, i)));
					cursor = i + 1;
				}
			}
			expressions.add(parse(split.subList(cursor, split.size())));
			return new ParallelNode(expressions);
		}
		
		for (String s : split) {
			if (QUANTIFIERS.contains(s)) {
				buffer.setQuantifier(s);
				buffer = update(buffer, expressions);
			} else if (startsWithDelimiter(s)) {
				buffer = update(buffer, expressions);
				if (s.length() <= 1) {
					String message = Templates.create()
							.add("Unmatched group delimiter {}")
							.with(s)
							.data(split)
							.build();
					throw new ParseException(message);
				}
				int endIndex = s.length() - 1;
				if (s.startsWith("[^")) {
					String substring = s.substring(2, endIndex);
					buffer.setNodes(parseRanges(substring));
					buffer.setParallel(true);
					buffer.setNegative(true);
				} else if (s.startsWith("[")) {
					String substring = s.substring(1, endIndex);
					buffer.setNodes(parseRanges(substring));
					buffer.setParallel(true);
				} else if (s.startsWith("(?:")) {
					String substring = s.substring(3, endIndex);
					List<String> list = split(substring);
					Expression exp = parse(list);
					List<Expression> exps = getChildrenOrExpression(exp);
					buffer.setNodes(exps);
				} else {
					String substring = s.substring(1, endIndex);
					List<String> list = split(substring);
					Expression exp = parse(list);
					List<Expression> exps = getChildrenOrExpression(exp);
					buffer.setNodes(exps);
					buffer.setCapturing(true);
				}
			} else {
				buffer = update(buffer, expressions);
				buffer.setTerminal(ESCAPES.containsKey(s) ? ESCAPES.get(s) : s);
			}
		}
		update(buffer, expressions);
		if (expressions.size() == 1) {
			return expressions.get(0);
		} else {
			return new ParentNode(expressions);
		}
	}

	@NonNull
	private List<Expression> parseRanges(String list) {
		List<String> list1 = splitAlt(list);
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
									.with(list)
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
								.with(list)
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
		
		// Replace escapes with literals
		for (int i = 0; i < list2.size(); i++) {
			for (Map.Entry<String, String> entry : ESCAPES.entrySet()) {
				if (list2.get(i).equals(entry.getKey())) {
					list2.set(i, entry.getValue());
					break;
				}
			}
		}

		// Enforce uniqueness of strings
		Set<String> set = new HashSet<>(list2);
		List<Expression> parsedChildren = new ArrayList<>();
		for (String item : set) {
			if (Splitter.parseParens(item, DELIMITERS_ALT, new HashSet<>(), 0) >= 0) {
				parsedChildren.add(parseExpression(item));
			} else {
				parsedChildren.add(new TerminalNode(item));
			}
		}

		return parsedChildren;
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
		Set<String> specials = new HashSet<>();
		specials.addAll(CLASSES.keySet());
		specials.addAll(ESCAPES.keySet());
		
		List<String> list = Splitter.toList(string, DELIMITERS, specials);
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			if (CLASSES.containsKey(s)) {
				list.set(i, CLASSES.get(s));
			}
		}
		return list;
	}

	@NonNull
	private static List<String> splitAlt(String string) {
		Set<String> specials = new HashSet<>();
		specials.addAll(CLASSES.keySet());
		specials.addAll(ESCAPES.keySet());
		
		List<String> list = Splitter.toList(string, DELIMITERS_ALT, specials);
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
	public String subSequence(@NonNull String sequence, int start, int end) {
		return sequence.substring(start, end);
	}

	@NonNull
	@Override
	public String concatenate(
			@NonNull String sequence1, 
			@NonNull String sequence2
	) {
		return sequence1 + sequence2;
	}

	@NonNull
	@Override
	public String replaceGroups(
			@NonNull String input, @NonNull Match<String> match
	) {
		StringBuilder sb = new StringBuilder();
		StringBuilder number = new StringBuilder();
		
		boolean inGroup = false;
		
		int cursor = 0;
		int i = 0;
		while (i < input.length()) {
			char c = input.charAt(i);
			if (0x30 <= c && c < 0x3A && inGroup) {
				number.append(c);
				i++;
				cursor = i;
			} else {
				// parse and append group data
				if (number.length() > 0) {
					int groupNumber = Integer.parseInt(number.toString());
					String group = match.group(groupNumber);
					if (group != null) {
						sb.append(group);
					}
					// clear buffer
					number = new StringBuilder();
				}

				if (c == '$') {
					inGroup = true;
					if (cursor != i) {
						sb.append(input.substring(cursor, i));
					}
					i++;
					cursor = i;
				} else {
					inGroup = false;
					i++;
				}
			}
		}

		sb.append(input.substring(cursor));

		// parse and append group data
		if (number.length() > 0) {
			int groupNumber = Integer.parseInt(number.toString());
			String group = match.group(groupNumber);
			if (group != null) {
				sb.append(group);
			}
		}
		return sb.toString();
	}
}
