package org.didelphis.language.automata.parsers;

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
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.utilities.Splitter;
import org.didelphis.utilities.Templates;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class {@code DidelphisBaseParser}
 *
 * @author Samantha Fiona McCabe
 * @date 8/25/18
 * @see StringParser
 * @see SequenceParser
 * @since 0.3.0
 */
@ToString
@FieldDefaults (makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class AbstractDidelphisParser<T> implements LanguageParser<T> {

	static Map<String, String> DELIMITERS;
	static Set<String> QUANTIFIERS;
	static Expression START_EXP = new TerminalNode("#[");
	static Expression END_EXP = new TerminalNode("]#");

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

	@NonNull
	@Override
	@Contract ("_,_ -> new")
	public Expression parseExpression(@NonNull String exp,
			@NonNull ParseDirection direction) {

		validate(exp);

		Expression expression = parse(exp, split(exp));
		if (direction == ParseDirection.BACKWARD) {
			expression = expression.reverse();
		}

		replaceLast(expression);
		replaceFirst(expression);

		if (!expression.hasChildren() && "#".equals(expression.getTerminal())) {
			return new TerminalNode("]#");
		}

		return expression;
	}

	private Expression parse(@NonNull String rawExp,
			@NonNull List<String> split) {
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
					String message = Templates.create()
							.add("Unmatched group delimiter {}")
							.with(s)
							.data(rawExp)
							.build();
					throw new ParseException(message);
				}
				String substring = s.substring(1, s.length() - 1).trim();
				String delimiter = s.substring(0, 1);
				if (delimiter.equals("{")) {
					List<String> elements = Splitter.whitespace(substring);
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

	/**
	 * Checks for the presence of illegal sub-parsers in the expression which
	 * are unambiguous errors.
	 *
	 * @param string an expression to be checked for basic errors
	 *
	 * @throws ParseException if basic structural errors are found in the
	 * 		expression. Such errors include:
	 * 		<ul>
	 * 		<li>Double negations ({@code !!}</li>
	 * 		<li>Negation of a quantifier({@code !*}, {@code !?}</li>
	 * 		<li>Multiple quantification ({@code *?}, {@code ?+}</li>
	 * 		<li>Quantification of a boundary {@code #?}</li>
	 * 		<li>Negation of a boundary {@code !#}</li>
	 * 		</ul>
	 */
	private static void validate(@NonNull String string) {
		for (int i = 0; i < string.length() - 1; i++) {
			String p = String.valueOf(string.charAt(i));
			String s = String.valueOf(string.charAt(i + 1));
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
			if (QUANTIFIERS.contains(s) && p.equals("#") ||
					p.equals("!") && s.equals("#")) {
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

	private static void replaceFirst(@NonNull Expression expression) {
		if (expression.hasChildren()) {
			List<Expression> children = expression.getChildren();
			if (!children.isEmpty()) {
				if (expression.isParallel()) {
					for (int i = 0; i < children.size(); i++) {
						Expression child = children.get(i);
						String terminal = child.getTerminal();
						if (Objects.equals(terminal, "#")) {
							children.set(i, START_EXP);
						} else {
							replaceFirst(child);
						}
					}
				} else {
					Expression first = children.get(0);
					if (first.getTerminal().equals("#")) {
						children.set(0, START_EXP);
					} else {
						replaceFirst(first);
					}
				}
			}
		}
	}

	private static void replaceLast(@NonNull Expression expression) {
		if (expression.hasChildren()) {
			List<Expression> children = expression.getChildren();
			if (!children.isEmpty()) {
				if (expression.isParallel()) {
					for (int i = 0; i < children.size(); i++) {
						Expression child = children.get(i);
						String terminal = child.getTerminal();
						if (Objects.equals(terminal, "#")) {
							children.set(i, END_EXP);
						} else {
							replaceLast(child);
						}
					}
				} else {
					int index = children.size() - 1;
					Expression last = children.get(index);
					if (last.getTerminal().equals("#")) {
						children.set(index, END_EXP);
					} else {
						replaceLast(last);
					}
				}
			}
		}
	}

	@NonNull
	private static Buffer update(@NonNull String expression,
			@NonNull Buffer buffer,
			@NonNull Collection<Expression> children) {
		if (!buffer.isEmpty()) {
			Expression ex = buffer.toExpression();
			if (ex == null) {
				String message = Templates.create()
						.add("Unable to parse {}")
						.with(expression)
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

	@Data
	@FieldDefaults (level = AccessLevel.PRIVATE)
	public static final class Buffer {

		boolean negative;
		boolean parallel;

		String quantifier = "";
		String terminal = "";

		List<Expression> children = new ArrayList<>();

		public boolean isEmpty() {
			return children.isEmpty() && terminal.isEmpty();
		}

		public @Nullable Expression toExpression() {
			if (children.isEmpty()) {
				return new TerminalNode(terminal, quantifier, negative);
			} else if (parallel) {
				return new ParallelNode(children, quantifier, negative);
			} else if (terminal == null || terminal.isEmpty()) {
				return new ExpressionNode(children, quantifier, negative);
			} else {
				return null;
			}
		}
	}
}
