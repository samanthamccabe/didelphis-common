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

import lombok.NonNull;

import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.phonetic.sequences.Sequence;

import java.util.List;

/**
 * Interface {@code Automaton}
 * <p>
 * Represents an automaton for accepting formal languages, such as finite state
 * automata.
 *
 * @param <S> Usually a sequential data type, such as {@link String} or {@link
 * 		Sequence}; this is the type of object provided to the automaton to be
 * 		checked
 *
 * @see org.didelphis.language.automata.statemachines.StateMachine
 * @see Regex
 *
 */
public interface Automaton<S> {

	/**
	 * Return a {@link Match} object representing the output of the automaton's
	 * attempt to match the input.
	 *
	 * @param input the input to be checked.
	 * @param start if applicable, the index at which to start checking.
	 *
	 * @return the resulting {@link Match} object
	 */
	@NonNull
	Match<S> match(@NonNull S input, int start);

	@NonNull
	Match<S> find(@NonNull S input);

	/**
	 * Splits the given input sequence around matches of this automaton.
	 * <p>
	 * The list returned by this method contains each subsequence of the input
	 * sequence that is terminated by another subsequence that matches this
	 * automaton or is terminated by the end of the input. The sequences in the
	 * list are in the order in which they occur in the input. If this automaton
	 * does not match any subsequence of the input then the resulting list has
	 * just one element, namely the input sequence itself.
	 *
	 * @see java.util.regex.Pattern#split(CharSequence, int)
	 *
	 * @param input the sequence to be split
	 * @param limit the maximum number of times the automaton is applied
	 * @return a list of sequences matched by this automaton
	 */
	@NonNull
	List<S> split(@NonNull S input, int limit);

	@NonNull
	default List<S> split(@NonNull S input) {
		return split(input, -1);
	}

	/**
	 *
	 * @param input
	 * @param replacement
	 * @return
	 */
	@NonNull
	S replace(@NonNull S input, @NonNull S replacement);

	default boolean matches(@NonNull S input) {
		return match(input).start() > -1;
	}

	@NonNull
	default Match<S> match(@NonNull S input) {
		return match(input, 0);
	}
}
