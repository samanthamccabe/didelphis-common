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

package org.didelphis.language.automata;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.parsing.RegexParser;
import org.didelphis.language.automata.statemachines.StandardStateMachine;
import org.didelphis.language.automata.statemachines.StateMachine;

import org.intellij.lang.annotations.Language;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Class {@code Regex}
 * <p>
 * A {@link Automaton} wrapper for the standard {@link Pattern} class.
 *
 */
@ToString
@EqualsAndHashCode
public class Regex implements Automaton<String> {

	private final StateMachine<String> automaton;

	public Regex(@Language ("RegExp") @NonNull String pattern) {
		this(pattern, false);
	}

	public Regex(@Language ("RegExp") @NonNull String pattern, boolean insensitive) {
		RegexParser parser = new RegexParser(insensitive);
		Expression exp = parser.parseExpression(pattern);
		automaton = StandardStateMachine.create("M0", exp, parser);
	}

	@NonNull
	@Override
	public Match<String> match(@NonNull String input, int start) {
		return automaton.match(input, start);
	}

	@NonNull
	@Override
	public Match<String> find(@NonNull String input) {
		return automaton.find(input);
	}

	@NonNull
	@Override
	public List<String> split(@NonNull String input, int limit) {
		return automaton.split(input, limit);
	}

	@NonNull
	@Override
	public String replace(@NonNull String input, @NonNull String replacement) {
		return automaton.replace(input, replacement);
	}
}
