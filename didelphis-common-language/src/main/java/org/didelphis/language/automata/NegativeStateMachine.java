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
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.interfaces.LanguageParser;
import org.didelphis.language.automata.interfaces.MachineMatcher;
import org.didelphis.language.automata.interfaces.StateMachine;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.structures.tuples.Triple;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Samantha Fiona McCabe
 * @date 1/30/2016
 */
public final class NegativeStateMachine<T> implements StateMachine<T> {

	private final String id;

	private final StateMachine<T> negative;
	private final StateMachine<T> positive;

	private NegativeStateMachine(
			@NonNull String id,
			@NonNull StateMachine<T> negative,
			@NonNull StateMachine<T> positive
	) {
		this.id = id;
		this.positive = positive;
		this.negative = negative;
	}

	@NonNull
	public static <T> StateMachine<T> create(
			@NonNull String id,
			@NonNull Expression expression,
			@NonNull LanguageParser<T> parser,
			@NonNull MachineMatcher<T> matcher,
			@NonNull ParseDirection direction
	) {
		// Create the actual branch, the one we don't want to match
		StateMachine<T> negative = StandardStateMachine.create(
				id + 'N',
				expression,
				parser,
				matcher,
				direction
		);
		StateMachine<T> positive = StandardStateMachine.create(
				id + 'P',
				expression,
				parser,
				matcher,
				direction
		);

		// This is less elegant that I'd prefer, but bear with me:
		// We will extract the graph and id-machine maps and then the graph for
		// *each* machine recursively, in order to replace each literal terminal
		// symbol with the dot (. / "accept-all") character
		buildPositiveBranch(parser, positive);

		return new NegativeStateMachine<>(id, negative, positive);
	}

	@NonNull
	@Override
	public LanguageParser<T> getParser() {
		return positive.getParser();
	}

	@NonNull
	@Override
	public MachineMatcher<T> getMatcher() {
		return positive.getMatcher();
	}

	@NonNull
	@Override
	public String getId() {
		return id;
	}

	@NonNull
	@Override
	public Map<String, Graph<T>> getGraphs() {
		Map<String, Graph<T>> map = new HashMap<>();
		map.putAll(positive.getGraphs());
		map.putAll(negative.getGraphs());
		return map;
	}

	@NonNull
	@Override
	public Set<Integer> getMatchIndices(int start, @NonNull T target) {

		Set<Integer> posIndices = positive.getMatchIndices(start, target);
		Set<Integer> negIndices = negative.getMatchIndices(start, target);

		if (!negIndices.isEmpty() && !posIndices.isEmpty()) {
			// Machine has matched both branches
			int pos = new TreeSet<>(posIndices).last();
			int neg = new TreeSet<>(negIndices).last();
			return pos == neg ? Collections.emptySet() : posIndices;
		} else if (!posIndices.isEmpty()) {
			return posIndices;
		} else {
			return Collections.emptySet();
		}

		/* This is left here as reference; not used because this method
		 * is not greedy - this was the first attempt, but does not work
		 * */
		// Complement --- remove negatives from positives
		//posIndices.removeAll(negIndices);
		//return posIndices;
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

	private static <T> void buildPositiveBranch(
			@NonNull LanguageParser<T> parser,
			@NonNull StateMachine<T> positive
	) {

		Graph<T> graph = positive.getGraphs().values().iterator().next();
		Graph<T> copy = new Graph<>(graph);

		graph.clear();

		for (Triple<String, T, Collection<String>> triple : copy) {

			T arc = triple.getSecondElement();
			Collection<String> targets = triple.getThirdElement();
			// lambda / epsilon transition
			String source = triple.getFirstElement();
			if (Objects.equals(arc, parser.epsilon())) {
				graph.put(source, parser.epsilon(), targets);
			} else if (parser.getSpecials().containsKey(arc.toString())) {
				T dot = parser.getDot();
				for (Integer length : collectLengths(parser, arc)) {
					buildDotChain(graph, source, targets, length, dot);
				}
			} else {
				graph.put(source, parser.getDot(), targets);
			}
		}
		
		if (positive instanceof StandardStateMachine) {
			for (StateMachine<T> machine : ((StandardStateMachine<T>) positive).getMachinesMap()
					.values()) {
				if (machine instanceof NegativeStateMachine) {
					// Unclear if this is allowed to happen
					// or if this is the desired behavior
					buildPositiveBranch(
							parser,
							((NegativeStateMachine<T>) machine).negative
					);
				} else {
					buildPositiveBranch(parser, machine);
				}
			}
		}
	}

	@NonNull
	private static <T> Iterable<Integer> collectLengths(
			@NonNull LanguageParser<T> parser,
			@NonNull T arc
	) {
		return parser.getSpecials()
				.get(arc.toString())
				.stream()
				.map(parser::lengthOf)
				.collect(Collectors.toSet());
	}

	private static <T> void buildDotChain(
			@NonNull Graph<T> graph,
			String key,
			Collection<String> endValues,
			int length,
			T dot
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
