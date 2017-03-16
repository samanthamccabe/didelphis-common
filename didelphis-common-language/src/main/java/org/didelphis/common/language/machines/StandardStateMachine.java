/**
 * ***************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe                                  *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 * http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * ****************************************************************************
 */

package org.didelphis.common.language.machines;

import org.didelphis.common.language.enums.ParseDirection;
import org.didelphis.common.language.exceptions.ParseException;
import org.didelphis.common.language.machines.interfaces.MachineMatcher;
import org.didelphis.common.language.machines.interfaces.MachineParser;
import org.didelphis.common.language.machines.interfaces.StateMachine;
import org.didelphis.common.structures.tuples.Tuple;
import org.didelphis.common.utilities.Split;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.didelphis.common.utilities.Patterns.template;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 3/7/2015
 */
public final class StandardStateMachine<T> implements StateMachine<T> {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(
			StandardStateMachine.class);

	private static final Pattern ILLEGAL = template("#$1|$1$1", "[*+?]");

	private static final int A_ASCII = 0x41; // dec 65 / 0x41

	private final MachineParser<T> parser;
	private final MachineMatcher<T> matcher;
	private final String id;
	private final String startStateId;
	private final Set<String> acceptingStates;
	private final Set<String> nodes;
	private final Map<String, StateMachine<T>> machinesMap;

	// {String (Node ID), Sequence (Arc)} --> String (Node ID)
	private final Graph<T> graph;

	@Override
	public MachineParser<T> getParser() {
		return parser;
	}

	@Override
	public MachineMatcher<T> getMatcher() {
		return matcher;
	}

	private StandardStateMachine(String id, MachineParser<T> parser,
			MachineMatcher<T> matcher) {
		this.parser = parser;
		this.id = id;
		this.matcher = matcher;

		startStateId = this.id + ":S";

		machinesMap = new HashMap<>();
		acceptingStates = new HashSet<>();
		nodes = new HashSet<>();
		graph = new Graph<>();
	}

	public static <T> StateMachine<T> create(String id, String expression,
			MachineParser<T> parser, MachineMatcher<T> matcher,
			ParseDirection direction) {
		checkBadQuantification(expression);
		StandardStateMachine<T> stateMachine = new StandardStateMachine<>(id,
				parser,
				matcher);
		List<Expression> expressions = parser.parseExpression(expression);
		stateMachine.parseExpression("", expressions, direction);
		return stateMachine;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Map<String, Graph<T>> getGraphs() {
		Map<String, Graph<T>> map = new HashMap<>();
		map.put(id, graph);
		return map;
	}

	@Override
	public Collection<Integer> getMatchIndices(int startIndex, T target) {
		Collection<Integer> indices = new HashSet<>();

		if (graph.isEmpty()) {
			indices.add(0);
			return indices;
		}

		// At the beginning of the process, we are in the start-state
		// so we find out what arcs leave the node.
		List<Tuple<Integer, String>> states = new ArrayList<>();

		// Add an initial state at the beginning of the sequence
		states.add(new Tuple<>(startIndex, startStateId));

		// if the condition is empty, it will always match
		List<Tuple<Integer, String>> swap = new ArrayList<>();
		while (!states.isEmpty()) {
			for (Tuple<Integer, String> state : states) {
				String currentNode = state.getRight();
				int index = state.getLeft();
				
				// Check internal state machines
				Collection<Integer> matchIndices;
				if (machinesMap.containsKey(currentNode)) {
					matchIndices = machinesMap.get(currentNode)
							.getMatchIndices(index, target);
				} else {
					matchIndices = new HashSet<>();
					matchIndices.add(index);
				}

				if (acceptingStates.contains(currentNode)) {
					indices.addAll(matchIndices);
				}

				if (graph.containsKey(currentNode)) {
					for (Integer mIndex : matchIndices) {
						// ----------------------------------------------------
						Map<T, Set<String>> map = graph.get(currentNode);
						for (Map.Entry<T, Set<String>> entry : map.entrySet()) {
							T key = entry.getKey();
							for (String node : entry.getValue()) {
								int match = matcher.match(target, key, mIndex);
								if (match >= 0) {
									swap.add(new Tuple<>(match, node));
								}
							}
						}
					}
				}
			}
			states = swap;
			swap = new ArrayList<>();
		}
		return indices;
	}

	public Graph<T> getGraph() {
		//noinspection ReturnOfCollectionOrArrayField
		return graph;
	}

	@Override
	public String toString() {
		return "StandardMachine{"
		       + "nodes: "
		       + nodes.size()
		       + ", machines: "
		       + machinesMap.size()
		       + '}';
	}

	// package only access
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	Map<String, StateMachine<T>> getMachinesMap() {
		// this needs to mutable:
		// see NegativeStateMachine.create(..)
		return machinesMap;
	}

	private StateMachine<T> createParallel(String machineId, String expression,
			ParseDirection direction) {
		StandardStateMachine<T> stateMachine = new StandardStateMachine<>(
				machineId,
				parser,
				matcher);
		int i = A_ASCII; // A
		for (String subExp : parseSubExpressions(expression)) {
			List<Expression> expressions = parser.parseExpression(subExp);
			String prefix = String.valueOf((char) i);
			// Machine is built to have one shared start-state and one end-state
			// for *each* individual branch
			stateMachine.parseExpression(prefix, expressions, direction);
			i++;
		}
		return stateMachine;
	}

	private void parseExpression(String branchPrefix,
			List<Expression> expressions, ParseDirection direction) {
		nodes.add(startStateId);

		if (direction == ParseDirection.BACKWARD) {
			Collections.reverse(expressions);
		}

		int nodeId = 0;
		String previous = startStateId;

		for (Iterator<Expression> it = expressions.iterator(); it.hasNext(); ) {
			Expression expression = it.next();
			nodeId++;
			String expr = expression.getExpression();
			String meta = expression.getMetacharacter();
			boolean negative = expression.isNegative();
			String current = id + ':' + branchPrefix + nodeId;

			nodes.add(current);

			if (negative) {
				StateMachine<T> machine = NegativeStateMachine.create(current,
						expr,
						parser,
						matcher,
						direction);
				machinesMap.put(current, machine);
				String nextNode = current + 'X';
				nodes.add(nextNode);
				previous = constructRecursiveNode(nextNode,
						previous,
						current,
						meta);
			} else {
				if (expr.startsWith("(")) {
					check(expr, ")");
					String substring = expr.substring(1, expr.length() - 1);
					StateMachine<T> machine = create(current,
							substring,
							parser,
							matcher,
							direction);
					machinesMap.put(current, machine);
					String nextNode = current + 'X';
					nodes.add(nextNode);
					previous = constructRecursiveNode(nextNode,
							previous,
							current,
							meta);
				} else if (expr.startsWith("{")) {
					check(expr, "}");
					// Remove braces
					String substring = expr.substring(1, expr.length() - 1);
					StateMachine<T> machine = createParallel(current,
							substring,
							direction);
					machinesMap.put(current, machine);
					String nextNode = current + 'X';
					nodes.add(nextNode);
					previous = constructRecursiveNode(nextNode,
							previous,
							current,
							meta);
				} else {
					previous = constructTerminalNode(previous,
							current,
							expr,
							meta);
				}
			}
			if (!it.hasNext()) {
				acceptingStates.add(previous);
			}
		}
	}

	private String constructRecursiveNode(String nextNode, String previousNode,
			String machineNode, String meta) {
		T e = parser.epsilon();
		switch (meta) {
			case "?":
				graph.add(previousNode, e, machineNode);
				graph.add(machineNode, e, nextNode);
				graph.add(previousNode, e, nextNode);
				break;
			case "*":
				graph.add(previousNode, e, machineNode);
				graph.add(machineNode, e, previousNode);
				graph.add(previousNode, e, nextNode);
				break;
			case "+":
				graph.add(previousNode, e, machineNode);
				graph.add(machineNode, e, nextNode);
				graph.add(nextNode, e, previousNode);
				break;
			default:
				graph.add(previousNode, e, machineNode);
				graph.add(machineNode, e, nextNode);
				break;
		}
		return nextNode;
	}

	private String constructTerminalNode(String previousNode,
			String currentNode, String exp, String meta) {
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

	private static void checkBadQuantification(String expression) {
		if (ILLEGAL.matcher(expression).find()) {
			throw new ParseException(
					"Illegal modification of boundary characters.",
					expression);
		}
	}

	private static void check(String expr, CharSequence closingBracket) {
		if (!expr.contains(closingBracket)) {
			throw new ParseException("Unmatched parenthesis", expr);
		}
	}

	private static Iterable<String> parseSubExpressions(String expression) {
		Collection<String> subExpressions = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			/*  */
			if (c == '{') {
				int index = Split.findClosingBracket(expression, '{', '}', i);
				buffer.append(expression.substring(i, index));
				i = index - 1;
			} else if (c == '(') {
				int index = Split.findClosingBracket(expression, '(', ')', i);
				buffer.append(expression.substring(i, index));
				i = index - 1;
			} else if (c != ' ') {
				buffer.append(c);
			} else if (buffer.length() > 0) { // No isEmpty() call available
				subExpressions.add(buffer.toString());
				buffer = new StringBuilder();
			}
		}

		if (buffer.length() > 0) {
			subExpressions.add(buffer.toString());
		}
		return subExpressions;
	}
}
