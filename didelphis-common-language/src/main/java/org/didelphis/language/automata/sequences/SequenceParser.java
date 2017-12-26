/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.language.automata.sequences;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.expressions.ExpressionNode;
import org.didelphis.language.automata.expressions.ParallelNode;
import org.didelphis.language.automata.expressions.TerminalNode;
import org.didelphis.language.automata.interfaces.LanguageParser;
import org.didelphis.language.parsing.FormatterMode;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.SequenceFactory;
import org.didelphis.language.phonetic.features.EmptyFeatureArray;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.segments.Segment;
import org.didelphis.language.phonetic.segments.StandardSegment;
import org.didelphis.language.phonetic.sequences.ImmutableSequence;
import org.didelphis.language.phonetic.sequences.Sequence;
import org.didelphis.structures.maps.GeneralMultiMap;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.utilities.Split;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class {@code SequenceParser}
 *
 * @param <T> the feature type used by language objects supported by a given
 * instance of this parser.
 *
 * @author Samantha Fiona McCabe
 * @date 2017-02-25
 * @since 0.1.0
 */
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SequenceParser<T> implements LanguageParser<Sequence<T>> {

	static Map<String, String> DELIMITERS;
	static Set<String> QUANTIFIERS;
	static Expression WORD_START = new TerminalNode("#[");
	static Expression WORD_END = new TerminalNode("]#");

	SequenceFactory<T> factory;
	MultiMap<String, Sequence<T>> specials;
	
	Sequence<T> wordStart;
	Sequence<T> wordEnd;
	Sequence<T> epsilon;

	public SequenceParser(@NonNull SequenceFactory<T> factory) {
		this(factory, new GeneralMultiMap<>());
	}

	public SequenceParser(
			@NonNull SequenceFactory<T> factory,
			@NonNull MultiMap<String, Sequence<T>> specials
	) {
		this.factory = factory;
		this.specials = specials;
		
		// Generate epsilon / lambda symbol
		FeatureModel<T> model = factory.getFeatureMapping().getFeatureModel();
		FeatureArray<T> array = new EmptyFeatureArray<>(model);
		Segment<T> segment = new StandardSegment<>("\uD835\uDF06", array);
		
		wordStart = new ImmutableSequence<>(new StandardSegment<>("#[", array));
		wordEnd   = new ImmutableSequence<>(new StandardSegment<>("]#", array));
		epsilon   = new ImmutableSequence<>(segment);
	}

	static {
		DELIMITERS = new HashMap<>();
		DELIMITERS.put("(", ")");
		DELIMITERS.put("{", "}");

		QUANTIFIERS = new HashSet<>();
		QUANTIFIERS.add("?");
		QUANTIFIERS.add("*");
		QUANTIFIERS.add("+");
	}

	@Override
	public Map<String, String> supportedDelimiters() {
		return Collections.unmodifiableMap(DELIMITERS);
	}

	@Override
	public Set<String> supportedQuantifiers() {
		return Collections.unmodifiableSet(QUANTIFIERS);
	}

	@Override
	public Sequence<T> getWordStart() {
		return wordStart;
	}

	@Override
	public Sequence<T> getWordEnd() {
		return wordEnd;
	}

	@Override
	public Sequence<T> transform(String expression) {
		if (expression.equals("#[")) return wordStart;
		if (expression.equals("]#")) return wordEnd;
		if (expression.isEmpty()) return epsilon;
		return factory.toSequence(expression);
	}

	@NonNull
	@Override
	public Expression parseExpression(String rawExpression) {

		validate(rawExpression);
		
		Expression expression = parse(rawExpression, split(rawExpression));
		
		replaceLast(expression);
		replaceFirst(expression);

		if (!expression.hasChildren() && "#".equals(expression.getTerminal())) {
			return new TerminalNode("]#");
		}
		
		return expression;
	}

	/**
	 * Checks for the presence of illegal sub-sequences in the expression which
	 * are unambiguous errors.
	 * @param chars
	 */
	private static void validate(CharSequence chars) {
		for (int i = 0; i < chars.length() - 1; i++) {
			String p = String.valueOf(chars.charAt(i));
			String s = String.valueOf(chars.charAt(i + 1));
			String message = null;
			
			if (p.equals("!") && s.equals("!")) {
				message = "Illegal double negation in expression {}{}";
			}
			if (p.equals("!") && QUANTIFIERS.contains(s)) {
				message = "Illegal use of quantification {}{}";
			}
			if (QUANTIFIERS.contains(s) && QUANTIFIERS.contains(p)) {
				message = "Illegal multiple quantification {}{}";
			}
			if (QUANTIFIERS.contains(s) && p.equals("#") 
					|| p.equals("!") && s.equals("#")) {
				message = "Illegal modification of boundary {}{}";
			}
			if (message!=null) {
				throw ParseException.builder()
						.add(message)
						.with(p, s)
						.data(chars)
						.build();
			}
		}
	}

	@NonNull
	@Override
	public Sequence<T> epsilon() {
		return epsilon;
	}

	@NonNull
	@Override
	public MultiMap<String, Sequence<T>> getSpecials() {
		return specials;
	}

	@NonNull
	@Override
	public Sequence<T> getDot() {
		return factory.getDotSequence();
	}

	@Override
	public int lengthOf(@NonNull Sequence<T> segments) {
		return segments.size();
	}

	@NonNull
	public SequenceFactory<T> getSequenceFactory() {
		return factory;
	}

	private Expression parse(String rawExp, List<String> split) {
		Collection<String> keys = specials.keys();
		
		Buffer buffer = new Buffer();
		List<Expression> expressions = new ArrayList<>();
		for (String s : split) {
			if (s.equals("!")) {
				buffer = update(rawExp, buffer, expressions);
				buffer.setNegative(true);
			} else if (QUANTIFIERS.contains(s)) {
				buffer.setQuantifier(s);
				buffer = update(rawExp, buffer, expressions);
			} else if (DELIMITERS.containsKey(s.substring(0, 1))) {
				buffer = update(rawExp, buffer, expressions);

				if (s.length() <= 1) {
					throw ParseException.builder()
							.add("Unmatched group delimiter {}")
							.with(s)
							.data(rawExp)
							.build();
				}

				String substring = s.substring(1, s.length() - 1).trim();
				String delimiter = s.substring(0, 1);
				if (delimiter.equals("{")) {

					List<String> elements = Split.whitespace(substring);

					List<Expression> children = elements.stream()
							.map(element -> split(element))
							.map(list -> parse(rawExp, list))
							.collect(Collectors.toList());

					buffer.setChildren(children);
					buffer.setParallel(true);
				} else {
					List<String> list = split(substring);
					Expression exp = parse(rawExp, list);
					buffer.setChildren(exp.getChildren());
				}
			} else {
				buffer = update(rawExp, buffer, expressions);
				buffer.setTerminal(s);
			}
		}

		update(rawExp, buffer, expressions);
		return expressions.size() == 1
				? expressions.get(0)
				: new ExpressionNode(expressions);
	}

	@NonNull
	private List<String> split(String substring) {
		FormatterMode formatter = factory.getFormatterMode();
		Collection<String> special = factory.getSpecialStrings();
		return formatter.split(substring, special);
	}
	
	private static void replaceFirst(Expression expression) {
		if (expression.hasChildren()) {
			List<Expression> children = expression.getChildren();
			if (!children.isEmpty()) {
				Expression first = children.get(0);
				if (first.getTerminal().equals("#")) {
					children.set(0, WORD_START);
				} else {
					replaceFirst(first);
				}
			}
		}
	}

	private static void replaceLast(Expression expression) {
		if (expression.hasChildren()) {
			List<Expression> children = expression.getChildren();
			if (!children.isEmpty()) {
				int index = children.size() - 1;
				Expression last = children.get(index);
				if (last.getTerminal().equals("#")) {
					children.set(index, WORD_END);
				} else {
					replaceLast(last);
				}
			}
		}
	}

	@NonNull
	private static Buffer update(
			String expression, 
			Buffer buffer, 
			Collection<Expression> children
	) {
		if (!buffer.isEmpty()) {
			Expression ex = buffer.toExpression();
			if (ex == null) {
				throw ParseException.builder()
						.add("Unable to parse ", expression)
						.data(buffer)
						.build();
			}
			children.add(ex);
			return new Buffer();
		} else {
			return buffer;
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	private static final class Buffer {
		
		boolean negative;
		boolean parallel;
		
		String  quantifier = "";
		String  terminal   = "";

		List<Expression> children = new ArrayList<>();

		private boolean isEmpty() {
			return children.isEmpty() && terminal.isEmpty();
		}

		private @Nullable Expression toExpression() {
			if (children.isEmpty()) {
				return new TerminalNode(terminal, quantifier, negative);
			} else if (parallel) {
				return  new ParallelNode(children, quantifier, negative);
			} else if (terminal == null || terminal.isEmpty()) {
				return new ExpressionNode(children, quantifier, negative);
			} else {
				return null;
			}
		}
	}
}
