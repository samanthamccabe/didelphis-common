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
 * @author Samantha Fiona McCabe
 * @date 10/17/17
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
