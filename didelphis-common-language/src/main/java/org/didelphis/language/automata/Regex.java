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

package org.didelphis.language.automata;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.matching.RegexMatcher;
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
 * @author Samantha Fiona McCabe
 */
@ToString
@EqualsAndHashCode
public class Regex implements Automaton<String> {

	private final StateMachine<String> automaton;

	public Regex(@Language ("RegExp") @NonNull String pattern) {
		this(pattern, false);
	}

	public Regex(@Language ("RegExp") @NonNull String pattern, boolean insensitive) {
		RegexParser parser = new RegexParser();
		RegexMatcher matcher = new RegexMatcher(parser, insensitive);
		Expression exp = parser.parseExpression(pattern);
		automaton = StandardStateMachine.create("M0", exp, parser, matcher);
	}

	@NonNull
	@Override
	public Match<String> match(@NonNull String input, int start) {
		return automaton.match(input, start);
	}

	@NonNull
	@Override
	public List<String> split(@NonNull String input, int limit) {
		return automaton.split(input, limit);
	}

	@NonNull
	@Override
	public String replace(
			@NonNull String input, @NonNull String replacement
	) {
		return automaton.replace(input, replacement);
	}
}
