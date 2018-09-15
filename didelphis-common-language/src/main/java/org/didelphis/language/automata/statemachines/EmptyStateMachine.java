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
import org.didelphis.language.automata.parsing.LanguageParser;
import org.didelphis.language.automata.matching.LanguageMatcher;
import org.didelphis.language.automata.matching.BasicMatch;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.utilities.Templates;

import java.util.Collections;
import java.util.List;
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
public final class EmptyStateMachine<S> implements StateMachine<S> {

	private static final EmptyStateMachine<?> MACHINE
			= new EmptyStateMachine<>();

	// The wildcard-extends is necessary to avoid an incompatible type error
	// even though Graph itself cannot be extended
	@SuppressWarnings ("TypeParameterExtendsFinalClass") 
	private static final Map<String, ? extends Graph<?>> EMPTY_MAP = Collections
			.emptyMap();

	@NonNull
	@SuppressWarnings("unchecked")
	public static <S> StateMachine<S> getInstance() {
		return (StateMachine<S>) MACHINE;
	}

	@NonNull
	@Override
	public LanguageParser<S> getParser() {
		String message = Templates.create()
				.add("Empty state machine has no associated parser")
				.build();
		throw new UnsupportedOperationException(message);
	}

	@NonNull
	@Override
	public LanguageMatcher<S> getMatcher() {
		return (x, y, i) -> 0;
	}

	@Override
	public String getId() {
		return "Empty State Machine";
	}

	@NonNull
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Graph<S>> getGraphs() {
		return (Map<String, Graph<S>>) EMPTY_MAP;
	}

	@NonNull
	@Override
	public Map<String, StateMachine<S>> getStateMachines() {
		return Collections.emptyMap();
	}


	@NonNull
	@Override
	public Match<S> match(@NonNull S input, int start) {
		return new BasicMatch<>(input, 0, start);
	}

	@NonNull
	@Override
	public List<S> split(@NonNull S input, int limit) {
		return Collections.singletonList(input);
	}

	@NonNull
	@Override
	public S replace(@NonNull S input, @NonNull S replacement) {
		return input; // no-op
	}
}
