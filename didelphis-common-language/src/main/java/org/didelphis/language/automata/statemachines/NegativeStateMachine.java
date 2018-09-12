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

import lombok.NonNull;
import org.didelphis.language.automata.Graph;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.parsing.LanguageParser;
import org.didelphis.language.automata.matching.LanguageMatcher;
import org.didelphis.language.automata.matching.BasicMatch;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.structures.tuples.Triple;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Samantha Fiona McCabe
 * @date 1/30/2016
 */
public final class NegativeStateMachine<S> implements StateMachine<S> {

	private final String id;

	private final StateMachine<S> negative;
	private final StateMachine<S> positive;

	private NegativeStateMachine(
			@NonNull String id,
			@NonNull StateMachine<S> negative,
			@NonNull StateMachine<S> positive
	) {
		this.id = id;
		this.positive = positive;
		this.negative = negative;
	}

	@NonNull
	public static <S> StateMachine<S> create(
			@NonNull String id,
			@NonNull Expression expression,
			@NonNull LanguageParser<S> parser,
			@NonNull LanguageMatcher<S> matcher,
			@NonNull List<Expression> captures
	) {
		// Create the actual branch, the one we don't want to match
		StateMachine<S> negative = StandardStateMachine.create(
				'N' + id,
				expression,
				parser,
				matcher,
				captures
		);
		
		StateMachine<S> positive = StandardStateMachine.create(
				'P' + id,
				expression,
				parser,
				matcher,
				captures
		);

		// This is less elegant that I'd prefer, but bear with me:
		// We will extract the graph and id-machine maps and then the graph for
		// *each* machine recursively, in order to replace each literal terminal
		// symbol with the dot (. / "accept-all") character
		// TODO: this can probably done by replacing all terminals in the expression tree
		buildPositiveBranch(parser, positive);

		return new NegativeStateMachine<>(id, negative, positive);
	}

	@NonNull
	@Override
	public LanguageParser<S> getParser() {
		return positive.getParser();
	}

	@NonNull
	@Override
	public LanguageMatcher<S> getMatcher() {
		return positive.getMatcher();
	}

	@NonNull
	@Override
	public String getId() {
		return id;
	}

	@NonNull
	@Override
	public Map<String, Graph<S>> getGraphs() {
		Map<String, Graph<S>> map = new HashMap<>();
		map.putAll(positive.getGraphs());
		map.putAll(negative.getGraphs());
		return map;
	}

	@NonNull
	@Override
	public Map<String, StateMachine<S>> getStateMachines() {
		Map<String, StateMachine<S>> map = new HashMap<>();
		map.put("POSITIVE", positive);
		map.put("NEGATIVE", negative);
		return map;
	}

	@Override
	public String toString() {
		return "NegativeStateMachine{"
				+ "negative="
				+ negative
				+ ", "
				+ "positive="
				+ positive
				+ '}';
	}

	@NonNull
	@Override
	public Match<S> match(@NonNull S input, int start) {

		BasicMatch<S> match = new BasicMatch<>(input, -1, -1);

		if (start >= getParser().lengthOf(input)) {
			return match;
		}

		Match<S> pMatch = positive.match(input, start);
		Match<S> nMatch = negative.match(input, start);

		return pMatch.end() == nMatch.end() ? match : pMatch;
	}

	private static <S> void buildPositiveBranch(
			@NonNull LanguageParser<S> parser,
			@NonNull StateMachine<S> positive
	) {

		Graph<S> graph = positive.getGraphs().values().iterator().next();
		Graph<S> copy = new Graph<>(graph);

		graph.clear();

		for (Triple<String, S, Collection<String>> triple : copy) {

			S arc = triple.getSecondElement();
			Collection<String> targets = triple.getThirdElement();
			// lambda / epsilon transition
			String source = triple.getFirstElement();
			if (Objects.equals(arc, parser.epsilon())) {
				graph.put(source, parser.epsilon(), targets);
			} else if (parser.getSpecialsMap().containsKey(arc.toString())) {
				S dot = parser.getDot();
				for (Integer length : collectLengths(parser, arc)) {
					buildDotChain(graph, source, targets, length, dot);
				}
			} else {
				graph.put(source, parser.getDot(), targets);
			}
		}
		
		if (positive instanceof StandardStateMachine) {
			for (StateMachine<S> machine : positive.getStateMachines()
					.values()) {
				if (machine instanceof NegativeStateMachine) {
					// Unclear if this is allowed to happen
					// or if this is the desired behavior
					buildPositiveBranch(
							parser,
							((NegativeStateMachine<S>) machine).negative
					);
				} else {
					buildPositiveBranch(parser, machine);
				}
			}
		}
	}

	@NonNull
	private static <S> Iterable<Integer> collectLengths(
			@NonNull LanguageParser<S> parser,
			@NonNull S arc
	) {
		return parser.getSpecialsMap()
				.get(arc.toString())
				.stream()
				.map(parser::lengthOf)
				.collect(Collectors.toSet());
	}

	private static <S> void buildDotChain(
			@NonNull Graph<S> graph,
			String key,
			Collection<String> endValues,
			int length,
			S dot
	) {
		String thisState = key;
		for (int i = 0; i < length - 1; i++) {
			String nextState = key + '-' + i;
			graph.add(thisState, dot, nextState);
			thisState = nextState;
		}
		graph.put(thisState, dot, endValues);
	}
}
