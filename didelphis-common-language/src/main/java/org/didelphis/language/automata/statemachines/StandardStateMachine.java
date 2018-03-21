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
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.Graph;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.interfaces.LanguageParser;
import org.didelphis.language.automata.matchers.LanguageMatcher;
import org.didelphis.language.automata.matches.BasicMatch;
import org.didelphis.language.automata.matches.Match;
import org.didelphis.language.parsing.ParseDirection;
import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * @author Samantha Fiona McCabe
 * @date 3/7/2015
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class StandardStateMachine<T> implements StateMachine<T> {
	
	static int A_ASCII = 0x41; // dec 65 / 0x41

	LanguageParser<T> parser;
	LanguageMatcher<T> matcher;
	ParseDirection direction;
	String id;
	String startStateId;
	Collection<String> acceptingStates;
	Map<String, StateMachine<T>> machinesMap;

	// {String (Node ID), Sequence (Arc)} --> String (Node ID)
	Graph<T> graph;

	private StandardStateMachine(
			String id,
			LanguageParser<T> parser,
			LanguageMatcher<T> matcher,
			ParseDirection direction
	) {
		this.parser = parser;
		this.id = id;
		this.matcher = matcher;
		this.direction = direction;

		startStateId = this.id + "-S";
	
		machinesMap = new HashMap<>();
		acceptingStates = new HashSet<>();
		graph = new Graph<>();
	}

	@NonNull
	public static <T> StateMachine<T> create(
			@NonNull String id,
			@NonNull Expression expression,
			@NonNull LanguageParser<T> parser,
			@NonNull LanguageMatcher<T> matcher,
			ParseDirection direction
	) {

		StandardStateMachine<T> machine = new StandardStateMachine<>(
				id,
				parser,
				matcher,
				direction
		);
		
		if (direction == ParseDirection.BACKWARD) {
			expression = expression.reverse();
		}

		String accepting = machine.parseExpression(
				machine.startStateId, 
				0, 
				"O", 
				Collections.singletonList(expression));
		machine.acceptingStates.add(accepting);
		return machine;
	}

	@NonNull
	@Override
	public LanguageParser<T> getParser() {
		return parser;
	}

	@NonNull
	@Override
	public LanguageMatcher<T> getMatcher() {
		return matcher;
	}

	@Override
	public String getId() {
		return id;
	}

	@NonNull
	@Override
	public Map<String, Graph<T>> getGraphs() {
		Map<String, Graph<T>> map = new HashMap<>();
		map.put(id, graph);
		return map;
	}

	@NonNull
	@Override
	public Match<T> match(@NonNull T input, int start) {

		if (graph.isEmpty()) {
			return new BasicMatch<>(input, 0, 0);
		}

		Collection<Tuple<Integer,String>> states = new ArrayList<>();

		// At the beginning of the addToGraph, we are in the start-state, so
		// add an initial state at the beginning of the sequence
		states.add(new Couple<>(start, startStateId));

		// if the condition is empty, it will always match
		Collection<Tuple<Integer,String>> swap = new ArrayList<>();
		Set<Integer> indices = new HashSet<>();
		while (!states.isEmpty()) {
			for (Tuple<Integer, String> state : states) {
				
				String currentNode = state.getRight();
				int index = state.getLeft();

				// Check internal state automata
				Set<Integer> indicesToCheck = new HashSet<>();

				if (machinesMap.containsKey(currentNode)) {
					StateMachine<T> machine = machinesMap.get(currentNode);
					Match<T> match = machine.match(input, index);
					indicesToCheck.add(match.end());
				} else {
					indicesToCheck.add(index);
				}

				if (acceptingStates.contains(currentNode)) {
					indices.addAll(indicesToCheck);
				}

				if (graph.containsKey(currentNode)) {
					for (Integer mIndex : indicesToCheck) {
						if (mIndex > parser.lengthOf(input)) {
							continue;
						}
						swap.addAll(checkNode(mIndex, input, currentNode));
					}
				}
			}
			states = swap;
			swap = new ArrayList<>();
		}
		
		Comparator<Integer> comparator = Comparator.naturalOrder();
		int matchEnd = indices.stream().max(comparator).orElse(-1);
		int matchStart = matchEnd == -1 ? -1 : start;
		return new BasicMatch<>(input, matchStart, matchEnd);
	}

	@Override
	public String toString() {
		return "StandardStateMachine{" + id + '}';
	}

	// package only access TODO: seems like a problem
	@NonNull
	Map<String, StateMachine<T>> getMachinesMap() {
		// this needs to mutable:
		// see NegativeStateMachine.create(..)
		return machinesMap;
	}

	/**
	 * Evaluates the inputs against the arcs leaving the current node and if
	 * successful, will add to the output indices
	 *
	 * @param index       the current index within the input
	 * @param input       the input data being consumed by this automaton
	 * @param currentNode the current state machine node where evaluation is
	 *                    taking place
	 *
	 * @return a collection of new states
	 */
	private Collection<Tuple<Integer, String>> checkNode(
			int index,
			@NonNull T input,
			@NonNull String currentNode
	) {
		Collection<Tuple<Integer, String>> indices = new HashSet<>();
		Map<T, Collection<String>> map = graph.get(currentNode);
		for (Entry<T, Collection<String>> entry : map.entrySet()) {
			T arc = entry.getKey();
			Collection<String> value = entry.getValue();
			for (String node : value) {
				
				if (Objects.equals(arc, parser.epsilon())) {
					indices.add(new Couple<>(index, node));
				} else if (Objects.equals(arc, parser.getDot()) && parser.lengthOf(input) > 0) {
					indices.add(new Couple<>(index + 1, node));
				} else if (Objects.equals(arc, parser.getWordStart()) && index == 0) {
					indices.add(new Couple<>(0, node));
				} else if (Objects.equals(arc, parser.getWordEnd()) && index == parser.lengthOf(input)) {
					indices.add(new Couple<>(index, node));
				} else {
					if (matcher.matches(input, arc, index)) {
						int i = parser.lengthOf(arc) + index;
						indices.add(new Couple<>(i, node));
					}
				}
			}
		}
		return indices;
	}

	@NonNull
	private String createParallel(String start, int index, @NonNull Expression expression) {
		int i = A_ASCII; // A
		String output = start + "-Out";
		for (Expression child : expression.getChildren()) {
			String prefix = String.valueOf((char) i);
			// Machine is built to have one shared start-state and one end-state
			// for *each* individual branch
			String closingState = parseExpression(start,
					index,
					prefix + '-' + start,
					Collections.singletonList(child)
			);
			i++;
			graph.add(closingState, parser.epsilon(), output);
		}
		return output;
	}

	private String parseExpression(
			String start, 
			int startingIndex,
			@NonNull String prefix, 
			@NonNull Iterable<Expression> expressions) {

		int nodeId = startingIndex;
		String previous = start;
		for (Expression expression : expressions) {
			nodeId++;
			
			String meta = expression.getQuantifier();
			boolean negative = expression.isNegative();
			
			String current = prefix + '-' + nodeId;

			if (negative) {
				createNegative(expression, current);
				String nextNode = current + 'X';
				previous = constructNegativeNode(nextNode,
						previous,
						current,
						meta);
			} else {
				if (expression.hasChildren()) {
					if (expression.isParallel()) {
						graph.add(previous, parser.epsilon(), current);
						String endNode = createParallel(current, nodeId, expression);
						previous = constructRecursiveNode(endNode, current, meta);
					} else {
						graph.add(previous, parser.epsilon(), current);
						String endNode = parseExpression(current,
								nodeId,
								"G-" + current,
								expression.getChildren());
						previous = constructRecursiveNode(endNode, current, meta);
					}	
				} else {
					previous = constructTerminalNode(previous,
							current,
							expression.getTerminal(),
							meta);
				}
			}
		}
		return previous;
	}

	private void createNegative(Expression expression, String current) {
		Expression negated = expression.withNegative(false).withQuantifier("");
		StateMachine<T> machine = NegativeStateMachine.create(current,
				negated,
				parser,
				matcher,
				direction);
		machinesMap.put(current, machine);
	}

	private String constructNegativeNode(String end, String start,
			String machine, @NonNull String meta) {
		// All automata contain this arc
		graph.add(start, parser.epsilon(), machine);
		addToGraph(start, end, machine, meta);
		return end;
	}

	private void addToGraph(String start, String end, String machine, String meta) {
		T e = parser.epsilon();
		switch (meta) {
			case "?":
				graph.add(machine, e, end);
				graph.add(start, e, end);
				break;
			case "*":
				graph.add(machine, e, start);
				graph.add(start, e, end);
				break;
			case "+":
				graph.add(machine, e, end);
				graph.add(end, e, start);
				break;
			default:
				graph.add(machine, e, end);
				break;
		}
	}

	@NonNull
	private String constructRecursiveNode(String machineNode, String startNode,
			@NonNull String meta) {
		String endNode = startNode + 'X';
		addToGraph(startNode, endNode, machineNode, meta);
		return endNode;
	}

	@NonNull
	private String constructTerminalNode(
			@NonNull String previousNode,
			@NonNull String currentNode, 
			@NonNull String exp, 
			@NonNull String meta) {
		T t = parser.transform(exp);
		T e = parser.epsilon();
		String referenceNode;
		switch (meta) {
			case "?":
				graph.add(previousNode, t, currentNode);
				graph.add(previousNode, e, currentNode);
				referenceNode = currentNode;
				break;
			case "*":
				graph.add(previousNode, t, previousNode);
				graph.add(previousNode, e, currentNode);
				referenceNode = currentNode;
				break;
			case "+":
				graph.add(previousNode, t, currentNode);
				graph.add(currentNode, e, previousNode);
				referenceNode = currentNode;
				break;
			default:
				graph.add(previousNode, t, currentNode);
				referenceNode = currentNode;
				break;
		}
		return referenceNode;
	}
}
