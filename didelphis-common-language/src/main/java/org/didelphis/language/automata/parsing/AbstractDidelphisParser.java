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

import lombok.NonNull;
import lombok.ToString;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.expressions.ParentNode;
import org.didelphis.language.automata.expressions.TerminalNode;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.language.parsing.ParseException;
import org.didelphis.utilities.Splitter;
import org.didelphis.utilities.Templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.didelphis.language.automata.parsing.LanguageParser.*;

/**
 * Abstract Class {@code DidelphisBaseParser}
 *
 * @see StringParser
 * @see SequenceParser
 * @since 0.3.0
 */
@ToString
public abstract class AbstractDidelphisParser<T> implements LanguageParser<T> {

	private static final Expression START_EXP = new TerminalNode("#[");
	private static final Expression END_EXP   = new TerminalNode("]#");

	private static final Set<String> QUANTIFIERS;
	static {
		QUANTIFIERS = new HashSet<>();
		QUANTIFIERS.add("?");
		QUANTIFIERS.add("*");
		QUANTIFIERS.add("+");
	}

	@NonNull
	@Override
	public Set<String> supportedQuantifiers() {
		return Collections.unmodifiableSet(QUANTIFIERS);
	}

	protected abstract List<String> split(String string);

	@NonNull
	@Override
	public Expression parseExpression(
			@NonNull String expression,
			@NonNull ParseDirection direction
	) {
		validate(expression);

		Expression exp;
		try {
			List<String> strings = split(expression);
			exp = parse(strings);
			if (direction == ParseDirection.BACKWARD) {
				exp = exp.reverse();
			}
			replaceLast(exp);
			replaceFirst(exp);
		} catch (ParseException e) {
			String message = Templates.create()
					.add("Failed to parse expression {}")
					.with(expression)
					.build();
			throw new ParseException(message, e);
		}

		if (!exp.hasChildren() && "#".equals(exp.getTerminal())) {
			return new TerminalNode("]#");
		}

		// Fix the node IDs
		return Expression.rewriteIds(exp, "0");
	}

	private Expression parse(@NonNull List<String> split) {
		ParserBuffer buffer = new ParserBuffer();
		List<Expression> expressions = new ArrayList<>();
		for (String s : split) {
			if (s.equals("!")) {
				buffer = update(buffer, expressions);
				buffer.setNegative(true);
			} else if (QUANTIFIERS.contains(s)) {
				buffer.setQuantifier(s);
				buffer = update(buffer, expressions);
			} else if (startsDelimiter(s)) {
				buffer = update(buffer, expressions);
				if (s.length() <= 2) {
					String message = Templates.create()
							.add("Unmatched delimiter or empty group {}")
							.with(s)
							.build();
					throw new ParseException(message);
				}
				String substring = s.substring(1, s.length() - 1).trim();
				if (s.startsWith("[")) {
					buffer = update(buffer, expressions);
					buffer.setTerminal(s);
				} else if (s.startsWith("{")) {
					List<String> elements = Splitter.whitespace(substring, supportedDelimiters());
					List<Expression> children = new ArrayList<>();
					for (String element : elements) {
						List<String> list = split(element);
						Expression parse = parse(list);
						children.add(parse);
					}
					buffer.setNodes(children);
					buffer.setParallel(true);
				} else if (s.startsWith("(?:")) {
					List<String> list = split(substring);
					Expression exp = parse(list);
					List<Expression> exps = getChildrenOrExpression(exp);
					buffer.setNodes(exps);
				} else {
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

	private boolean startsDelimiter(String string) {
		for (String s : supportedDelimiters().keySet()) {
			if (string.startsWith(s)) return true;
		}
		return false;
	}

	/**
	 * Checks for the presence of illegal sub-patterns in the expression which
	 * are unambiguous errors.
	 *
	 * @param string an expression to be checked for basic errors
	 *
	 * @throws ParseException if basic structural errors are found in the
	 *      expression. Such errors include:
	 *      <ul>
	 *      <li>Negation of the dot {@code . } character</li>
	 *      <li>Double negations ({@code !!}</li>
	 *      <li>Negation of a quantifier({@code !*}, {@code !?}</li>
	 *      <li>Multiple quantification ({@code *?}, {@code ?+}</li>
	 *      <li>Quantification of a boundary {@code #?}</li>
	 *      <li>Negation of a boundary {@code !#}</li>
	 *      </ul>
	 */
	private static void validate(@NonNull String string) {

		if (!string.isEmpty()) {
			String subString = string.substring(0, 1);
			if (QUANTIFIERS.contains(subString)) {
				String template = Templates.create()
						.add("Expression cannot start with quantifier!")
						.data(string)
						.build();
				throw new ParseException(template);
			}
		}

		for (int i = 0; i < string.length() - 1; i++) {
			String p = "" + string.charAt(i);
			String s = "" + string.charAt(i + 1);
			String message = null;

			if(p.equals("!") && s.equals(".")) {
				message = "Illegal negation of dot in expression {}{}";
			}
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
}
