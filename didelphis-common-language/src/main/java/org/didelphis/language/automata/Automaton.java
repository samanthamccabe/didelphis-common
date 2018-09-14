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
 * @see org.didelphis.language.automata.JavaPatternAutomaton
 * 
 * @author Samantha Fiona McCabe
 * @date 10/17/17
 */
@FunctionalInterface
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

	default boolean matches(@NonNull S input) {
		return match(input).start() > -1;
	}
	
	@NonNull
	default Match<S> match(@NonNull S input) {
		return match(input, 0);
	}
}