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

package org.didelphis.language.automata.statemachines;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.didelphis.language.automata.Graph;
import org.didelphis.language.automata.interfaces.LanguageParser;
import org.didelphis.language.automata.matchers.LanguageMatcher;
import org.didelphis.language.automata.matches.BasicMatch;
import org.didelphis.language.automata.matches.Match;
import org.didelphis.utilities.Templates;

import java.util.Collections;
import java.util.Map;

/**
 * Class {@code EmptyStateMachine}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-06-17
 * @since 0.1.0
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyStateMachine<T> implements StateMachine<T> {

	private static final EmptyStateMachine<?> MACHINE
			= new EmptyStateMachine<>();

	// The wildcard-extends is necessary to avoid an incompatible type error
	// even though Graph itself cannot be extended
	@SuppressWarnings ("TypeParameterExtendsFinalClass") 
	private static final Map<String, ? extends Graph<?>> EMPTY_MAP = Collections
			.emptyMap();

	@NonNull
	@SuppressWarnings("unchecked")
	public static <T> StateMachine<T> getInstance() {
		return (StateMachine<T>) MACHINE;
	}

	@NonNull
	@Override
	public LanguageParser<T> getParser() {
		String message = Templates.create()
				.add("Empty state machine has no associated parser")
				.build();
		throw new UnsupportedOperationException(message);
	}

	@NonNull
	@Override
	public LanguageMatcher<T> getMatcher() {
		return (x, y, i) -> 0;
	}

	@Override
	public String getId() {
		return "Empty State Machine";
	}

	@NonNull
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Graph<T>> getGraphs() {
		return (Map<String, Graph<T>>) EMPTY_MAP;
	}


	@NonNull
	@Override
	public Match<T> match(@NonNull T input, int start) {
		return new BasicMatch<>(input, 0, start);
	}
}
