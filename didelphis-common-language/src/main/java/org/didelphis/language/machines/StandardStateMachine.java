/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.language.machines;

import org.didelphis.language.enums.ParseDirection;
import org.didelphis.language.exceptions.ParseException;
import org.didelphis.language.machines.interfaces.MachineMatcher;
import org.didelphis.language.machines.interfaces.MachineParser;
import org.didelphis.language.machines.interfaces.StateMachine;
import org.didelphis.structures.tuples.Tuple;
import org.didelphis.utilities.Split;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import static org.didelphis.utilities.Patterns.template;

/**
 * @author Samantha Fiona McCabe
 * Date: 3/7/2015
 */
public final class StandardStateMachine<T> implements StateMachine<T> {

	private static final Logger LOG = LoggerFactory.getLogger(
			StandardStateMachine.class);

	private static final Pattern ILLEGAL = template("#$1|$1$1", "[*+?]");

	private static final int A_ASCII = 0x41; // dec 65 / 0x41

	private final MachineParser<T> parser;
	private final MachineMatcher<T> matcher;
	private final ParseDirection direction;
	private final String id;
	private final String startStateId;
	private final Collection<String> acceptingStates;
	private final Set<String> nodes;
	private final Map<String, StateMachine<T>> machinesMap;

	// {String (Node ID), Sequence (Arc)} --> String (Node ID)
	private final Graph<T> graph;

	private StandardStateMachine(String id, MachineParser<T> parser,
			MachineMatcher<T> matcher, ParseDirection direction) {
		this.parser = parser;
		this.id = id;
		this.matcher = matcher;
		this.direction = direction;

		startStateId = this.id + "-S";

		machinesMap = new HashMap<>();
		acceptingStates = new HashSet<>();
		nodes = new HashSet<>();
		graph = new Graph<>();

		nodes.add(startStateId);
	}

	public static <T> StateMachine<T> create(String id, String expression,
			MachineParser<T> parser, MachineMatcher<T> matcher,
			ParseDirection direction) {
		checkBadQuantification(expression);
		StandardStateMachine<T> stateMachine = new StandardStateMachine<>(id,
				parser,
				matcher,
				direction);
		List<Expression> expressions = parser.parseExpression(expression);

		if (direction == ParseDirection.BACKWARD) {
			Collections.reverse(expressions);
		}

		String accepting = stateMachine.parseExpression(
				stateMachine.startStateId,
				0,
				"",
				expressions);
		stateMachine.acceptingStates.add(accepting);
		return stateMachine;
	}

	@Override
	public MachineParser<T> getParser() {
		return parser;
	}

	@Override
	public MachineMatcher<T> getMatcher() {
		return matcher;
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

		Collection<Tuple<Integer, String>> states = new ArrayList<>();

		// At the beginning of the process, we are in the start-state, so
		// add an initial state at the beginning of the sequence
		states.add(new Tuple<>(startIndex, startStateId));

		// if the condition is empty, it will always match
		Collection<Tuple<Integer, String>> swap = new ArrayList<>();
		while (!states.isEmpty()) {
			for (Tuple<Integer, String> state : states) {
				String currentNode = state.getRight();
				int index = state.getLeft();

				// Check internal state machines
				Collection<Integer> indicesToCheck;
				if (machinesMap.containsKey(currentNode)) {
					indicesToCheck = machinesMap.get(currentNode)
							.getMatchIndices(index, target);
				} else {
					indicesToCheck = new HashSet<>();
					indicesToCheck.add(index);
				}

				if (acceptingStates.contains(currentNode)) {
					indices.addAll(indicesToCheck);
				}

				if (graph.getDelegate().containsKey(currentNode)) {
					for (Integer mIndex : indicesToCheck) {
						// ----------------------------------------------------
						Map<T, Collection<String>> map = graph.getDelegate().get(currentNode);
						for (Entry<T, Collection<String>> entry : map.entrySet()) {
							T arc = entry.getKey();
							for (String node : entry.getValue()) {
								int match = matcher.match(target, arc, mIndex);
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

	@Override
	public String toString() {
		return "StandardStateMachine{" + id + '}';
	}

	// package only access
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	Map<String, StateMachine<T>> getMachinesMap() {
		// this needs to mutable:
		// see NegativeStateMachine.create(..)
		return machinesMap;
	}

	private String createParallel(String start, int index, String expression) {
		int i = A_ASCII; // A
		String output = start + "-Out";
		for (String subExp : parseSubExpressions(expression)) {
			Iterable<Expression> expressions = parser.parseExpression(subExp);
			String prefix = String.valueOf((char) i);
			// Machine is built to have one shared start-state and one end-state
			// for *each* individual branch
			String closingState = parseExpression(start,
					index,
					prefix + '-' + start,
					expressions);
			i++;
			graph.add(closingState, parser.epsilon(), output);
		}
		return output;
	}

	private String parseExpression(String start, int startingIndex,
			String prefix, Iterable<Expression> expressions) {

		int nodeId = startingIndex;
		String previous = start;
		for (Expression expression : expressions) {
			nodeId++;
			String expr = expression.getExpression();
			String meta = expression.getMetacharacter();
			boolean negative = expression.isNegative();
			String current = prefix + '-' + nodeId;
			nodes.add(current);

			if (negative) {
				createNegative(expr, current);
				String nextNode = current + 'X';
				nodes.add(nextNode);
				previous = constructNegativeNode(nextNode,
						previous,
						current,
						meta);
			} else {
				if (expr.startsWith("(")) {
					check(expr, ")");
					graph.add(previous, parser.epsilon(), current);
					String substring = expr.substring(1, expr.length() - 1);
					String endNode = parseExpression(current,
							nodeId,
							"G-" + current,
							parser.parseExpression(substring));
					previous = constructRecursiveNode(endNode, current, meta);
				} else if (expr.startsWith("{")) {
					check(expr, "}");
					graph.add(previous, parser.epsilon(), current);
					String substring = expr.substring(1, expr.length() - 1);
					String endNode = createParallel(current, nodeId, substring);
					previous = constructRecursiveNode(endNode, current, meta);
				} else {
					previous = constructTerminalNode(previous,
							current,
							expr,
							meta);
				}
			}
		}
		return previous;
	}

	private void createNegative(String expr, String current) {
		StateMachine<T> machine = NegativeStateMachine.create(current,
				expr,
				parser,
				matcher,
				direction);
		machinesMap.put(current, machine);
	}

	private String constructNegativeNode(String end, String start,
			String machine, String meta) {
		T e = parser.epsilon();
		// All machines contain this arc
		graph.add(start, e, machine);
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
		return end;
	}

	private String constructRecursiveNode(String machineNode, String startNode,
			String meta) {
		T e = parser.epsilon();

		String endNode = startNode + 'X';
		nodes.add(endNode);

		switch (meta) {
			case "?":
				graph.add(machineNode, e, endNode);
				graph.add(startNode, e, endNode);
				break;
			case "*":
				graph.add(machineNode, e, startNode);
				graph.add(startNode, e, endNode);
				break;
			case "+":
				graph.add(machineNode, e, endNode);
				graph.add(endNode, e, startNode);
				break;
			default:
				graph.add(machineNode, e, endNode);
				break;
		}
		return endNode;
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
