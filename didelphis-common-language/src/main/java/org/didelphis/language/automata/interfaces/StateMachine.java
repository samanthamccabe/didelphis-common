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

import org.didelphis.language.automata.Graph;
import lombok.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * @author Samantha Fiona McCabe
 * @date 2015-04-07
 */
@Deprecated
public interface StateMachine<T> {

	@NonNull
	LanguageParser<T> getParser();

	@NonNull MachineMatcher<T> getMatcher();

	String getId();

	/**
	 * Returns a map of {@link StateMachine} ids to its associated graph. This
	 * ensures accessibility for automata which contain multiple embedded state
	 * automata.
	 * @return a {@link Map},  from {@link StateMachine} id → {@link Graph}
	 */
	@NonNull
	Map<String, Graph<T>> getGraphs();
	
	/**
	 * Returns the indices
	 * @param start
	 * @param target
	 * @return
	 */
	@NonNull
	Set<Integer> getMatchIndices(int start, @NonNull T target);
}
