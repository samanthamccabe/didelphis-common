/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.automata.parsing;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.expressions.ParallelNode;
import org.didelphis.language.automata.expressions.ParentNode;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.structures.graph.Arc;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Splitter;
import org.didelphis.utilities.Templates;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.Collection;
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
 * @since 0.3.0
 */
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class RegexParser implements LanguageParser<String> {

	static Arc<String> DOT_ARC = new Arc<String>() {
		@Override
		public int match(String sequence, int index) {
			int length = sequence.length();
			return length > 0 && index < length ? index + 1 : -1;
		}

		@Override
		public String toString() {
			return ".";
		}
	};
	static Arc<String> EPSILON_ARC = new Arc<String>() {
		@Override
		public int match(String sequence, int index) {
			return index;
		}

		@Override
		public String toString() {
			return "";
		}
	};
	static Arc<String> WORD_START_ARC = new Arc<String>() {
		@Override
		public int match(String sequence, int index) {
			return index == 0 ? 0 : -1;
		}

		@Override
		public String toString() {
			return "^";
		}
	};
	static Arc<String> WORD_END_ARC = new Arc<String>() {
		@Override
		public int match(String sequence, int index) {
			int length = sequence.length();
			return index == length ? length : -1;
		}

		@Override
		public String toString() {
			return "$";
		}
	};

	static Map<String, String> DELIMITERS_ALT = new HashMap<>();
	static Map<String, String> DELIMITERS     = new HashMap<>();
	static Map<String, String> CLASSES        = new HashMap<>();
	static Map<String, String> ESCAPES        = new HashMap<>();
	
	static Set<String> QUANTIFIERS = new HashSet<>();

	static {
		DELIMITERS.put("[", "]");
		DELIMITERS.put("[^", "]");
		DELIMITERS.put("(", ")");
		DELIMITERS.put("(?:", ")");

		DELIMITERS_ALT.put("[", "]");
		DELIMITERS_ALT.put("[^", "]");

		CLASSES.put("\\d", "[0-9]");
		CLASSES.put("\\D", "[^0-9]");
		CLASSES.put("\\w", "[a-zA-Z0-9_]");
		CLASSES.put("\\W", "[^a-zA-Z0-9_]");
		CLASSES.put("\\s", "[ \t\f\r\n]");
		CLASSES.put("\\S", "[^ \t\f\r\n]");
		CLASSES.put("\\a", "[a-zA-Z]"); // custom
		CLASSES.put("\\A", "[^a-zA-Z]"); // custom

		CLASSES.put("[:alnum:]", "[A-Za-z0-9]");
		CLASSES.put("[:alpha:]", "[A-Za-z]");
		CLASSES.put("[:blank:]", "[ \t]");
		CLASSES.put("[:digit:]", "[0-9]");
		CLASSES.put("[:lower:]", "[a-z]");
		CLASSES.put("[:upper:]", "[A-Z]");
		CLASSES.put("[:xdigit:]", "[A-Fa-f0-9]");
		CLASSES.put("[:punct:]", "[!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-\\[\\]]");
		CLASSES.put("[:space:]", "[ \t\f\r\n\\v]");

		ESCAPES.put("\\$", "$");
		ESCAPES.put("\\\\", "\\");
		ESCAPES.put("\\|", "|");
		ESCAPES.put("\\[", "[");
		ESCAPES.put("\\]", "]");
		ESCAPES.put("\\(", "(");
		ESCAPES.put("\\)", ")");
		ESCAPES.put("\\.", ".");
		ESCAPES.put("\\?", "?");
		ESCAPES.put("\\*", "*");
		ESCAPES.put("\\+", "+");
		ESCAPES.put("\\-", "-");

		QUANTIFIERS.add("?");
		QUANTIFIERS.add("*");
		QUANTIFIERS.add("+");
	}

	private final boolean insensitive;

	public RegexParser() {
		this(false);
	}

	/**
	 *
	 * @param insensitive if {@code true} the parser is set up to generate case-
	 * insensitive state machines
	 */
	public RegexParser(boolean insensitive) {
		this.insensitive = insensitive;
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
	public String transform(String expression) {
		return expression;
	}

	@NonNull
	@Override
	public Arc<String> getArc(String arc) {

		if (arc.equals("^")) return WORD_START_ARC;
		if (arc.equals("$")) return WORD_END_ARC;
		if (arc.equals(".")) return DOT_ARC;

		if (arc.startsWith("[^")) {
			String substring = arc.substring(2, arc.length() - 1);
			return new NegativeArc(parseRanges(substring));
		}

		if (arc.startsWith("[")) {
			String substring = arc.substring(1, arc.length() - 1);
			return parseRanges(substring);
		}

		return new LiteralArc(arc, insensitive);
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

	@NonNull
	@Override
	public Arc<String> epsilon() {
		return EPSILON_ARC;
	}

	@NonNull
	@Override
	public MultiMap<String, String> getSpecialsMap() {
		return GeneralMultiMap.emptyMultiMap();
	}

	@NonNull
	@Override
	public Arc<String> getDot() {
		return DOT_ARC;
	}

	@Override
	public int lengthOf(@NonNull String t) {
		return t.length();
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
					buffer.setTerminal(s);
				} else if (s.startsWith("[")) {
					buffer.setTerminal(s);
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
				buffer.setTerminal(s);
			}
		}
		update(buffer, expressions);
		return expressions.size() == 1
				? expressions.get(0)
				: new ParentNode(expressions);
	}

	private boolean startsWithDelimiter(String s) {
		for (String delimiter : supportedDelimiters().keySet()) {
			if (s.startsWith(delimiter)) return true;
		}
		return false;
	}

	@NonNull
	private Arc<String> parseRanges(String list) {

		Set<String> specials = new HashSet<>();
		specials.addAll(CLASSES.keySet());
		specials.addAll(ESCAPES.keySet());

		List<String> list1 = Splitter.toList(list, DELIMITERS_ALT, specials);

		Set<String> set = new HashSet<>();
		Set<Arc<String>> arcs = new HashSet<>();
		for (int i = 0; i < list1.size(); ) {
			String s1 = list1.get(i);

			if (s1.startsWith("[^")) {
				int i1 = Splitter.parseParens(s1, DELIMITERS_ALT, specials, 0);
				if (i1 == s1.length()) {
					Arc<String> arc = parseRanges(s1.substring(2, i1 - 1));
					arcs.add(new NegativeArc(arc));
				}
				i++;
				continue;
			}

			if (s1.startsWith("[")) {
				int i1 = Splitter.parseParens(s1, DELIMITERS_ALT, specials, 0);
				if (i1 == s1.length()) {
					Arc<String> arc = parseRanges(s1.substring(1, i1 - 1));
					arcs.add(arc);
				}
				i++;
				continue;
			}

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

						set.add(String.valueOf(c1));
						while (c1 < c2) {
							c1++;
							set.add(String.valueOf(c1));
						}
						i += 2;
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
					set.add(s1);
				}
			} else {
				set.add(s1);
			}
			i++;
		}

		SetArc mainArc = new SetArc(substituteEscapes(set), insensitive);
		if (!arcs.isEmpty()) {
			arcs.add(mainArc);
			return new OrArc(arcs);
		}
		return mainArc;
	}

	@NonNull
	private static Set<String> substituteEscapes(Set<String> set) {
		Set<String> substituted = new HashSet<>();
		for (String item : set) {
			boolean retained = true;
			for (Map.Entry<String, String> entry : ESCAPES.entrySet()) {
				if (item.equals(entry.getKey())) {
					substituted.add(entry.getValue());
					retained = false;
					break;
				}
			}
			if (retained) {
				substituted.add(item);
			}
		}
		return substituted;
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

		if (!list.isEmpty()) {
			String string = list.get(0);
			if (QUANTIFIERS.contains(string)) {
				String template = Templates.create()
						.add("Expression cannot start with quantifier!")
						.data(list)
						.build();
				throw new ParseException(template);
			}
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

	private static final class LiteralArc implements Arc<String> {

		private final String literal;
		private final boolean insensitive;

		private LiteralArc(String literal, boolean insensitive) {
			this.insensitive = insensitive;
			String string = ESCAPES.containsKey(literal)
					? ESCAPES.get(literal)
					: literal;
			this.literal = insensitive ? string.toLowerCase() : string;
		}

		@Override
		public int match(String sequence, int index) {
			if (insensitive) {
				sequence = sequence.toLowerCase();
			}
			if (sequence.startsWith(literal, index)) {
				return index + literal.length();
			}
			return -1;
		}

		@Override
		public String toString() {
			return literal;
		}
	}

	private static final class SetArc implements Arc<String> {

		private final Collection<String> strings;
		private final boolean insensitive;

		private SetArc(Collection<String> strings, boolean insensitive) {
			this.insensitive = insensitive;
			if (insensitive) {
				Set<String> set = new HashSet<>();
				for (String string : strings) {
					set.add(string.toLowerCase());
				}
				this.strings = set;
			} else {
				this.strings = new HashSet<>(strings);
			}
		}

		@Override
		public int match(String sequence, int index) {

			if (insensitive) {
				sequence = sequence.toLowerCase();
			}

			for (String string : strings) {
				if (sequence.startsWith(string, index)) {
					return index + 1;
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return strings.toString();
		}
	}

	private static final class NegativeArc implements Arc<String> {

		private final Arc<String> arc;

		private NegativeArc(Arc<String> arc) {
			this.arc = arc;
		}

		@Override
		public int match(String sequence, int index) {
			int match = arc.match(sequence, index);
			if (match >= 0) {
				return -1;
			}
			return index + 1;
		}

		@Override
		public String toString() {
			return '~' + arc.toString();
		}
	}

	private static final class OrArc implements Arc<String> {
		private final Iterable<Arc<String>> arcs;

		private OrArc(Iterable<Arc<String>> arcs) {
			this.arcs = arcs;
		}

		@Override
		public int match(String sequence, int index) {
			for (Arc<String> arc : arcs) {
				int i = arc.match(sequence, index);
				if (i >= 0) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return arcs.toString();
		}
	}
}
