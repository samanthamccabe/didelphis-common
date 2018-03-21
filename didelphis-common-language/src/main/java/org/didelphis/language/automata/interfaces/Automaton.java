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

package org.didelphis.language.automata.interfaces;

import lombok.NonNull;
import org.didelphis.language.automata.matches.Match;

/**
 * Interface {@code Automaton}
 *
 * Represents an automaton for accepting formal languages, such as finite state
 * automata.
 * 
 * @param <T> The (usually sequential) data type being matched, such as {@link
 * String} or {@link org.didelphis.language.phonetic.sequences.Sequence}
 * 
 * @author Samantha Fiona McCabe
 * @date 10/17/17
 * @see java.util.regex.Pattern
 */
@FunctionalInterface
public interface Automaton<T> {

	/**
	 * Return a{@link Match} object representing the output of the automaton's 
	 * attempt to match the input.
	 * @param input the input to be checked.
	 * @param start if applicable, the index at which to start checking.
	 * @return the resulting {@link Match} object
	 */
	@NonNull 
	Match<T> match(@NonNull T input, int start);

	default boolean matches(@NonNull T input) {
		return match(input).start() > -1;
	}
	
	@NonNull
	default Match<T> match(@NonNull T input) {
		return match(input, 0);
	}
}
