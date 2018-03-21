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

package org.didelphis.language.automata.matchers;

import lombok.NonNull;

/**
 * Interface {@code MachineMatcher}
 *
 * A helper interface used by finite state automata to determine define when
 * a "match" has occurred; many implementations might 
 * 
 * @param <T> The type of object which represents state transitions.
 *
 * @author Samantha Fiona McCabe
 * @date 2017-02-23
 * @since 0.1.0
 */
@FunctionalInterface
public interface LanguageMatcher<T>  {

	/**
	 * Determines if the provided input matches the provided target in per the
	 * semantics of the implementation. This may be an exact or approximate
	 * match, or use operate like {@link String#startsWith(String)}
	 *
	 * @param input  the input to test
	 * @param target the data to test the input against
	 * @param index  the index of the input at which to evaluate the match
	 *
	 * @return true if the input "matches" the target per the documented
	 * semantics of the implementation
	 */
	boolean matches(@NonNull T input, @NonNull T target, int index);
}
