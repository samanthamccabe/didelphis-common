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
import org.didelphis.common.language.phonetic.SequenceFactory;
import org.didelphis.common.language.phonetic.sequences.BasicSequence;
import org.didelphis.common.language.phonetic.sequences.Sequence;
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
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 3/7/2015
 */
public class StateMachine implements Machine {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(StateMachine.class);

	public static final StateMachine EMPTY_MACHINE = new StateMachine();

	private static final Pattern ILLEGAL_PATTERN  = Pattern.compile("#(\\*|\\+|\\?)|(\\*|\\+|\\?)(\\*|\\+|\\?)");
	private static final int A_ASCII = 65;

	private final SequenceFactory factory;

	private final String      machineId;
	private final String      startStateId;
	private final Set<String> acceptingStates;
	private final Set<String> nodes;

	private final Map<String, Machine> machinesMap;

	private final Graph graph; // String (Node ID), Sequence (Arc) --> String (Node ID)

	public static StateMachine create(String id,
	                                  String expression,
	                                  SequenceFactory factoryParam, 
	                                  ParseDirection direction) {
		StateMachine stateMachine = new StateMachine(id, factoryParam);

		if (ILLEGAL_PATTERN.matcher(expression).find()) {
			throw new ParseException("Illegal modification of boundary characters.", expression);
		}

		List<Expression> expressions = Expression.getExpressions(
			  expression,
			  factoryParam.getSpecialStrings(),
			  factoryParam.getFormatterMode()
		);
		stateMachine.parseExpression("", expressions, direction);
		return stateMachine;
	}

	private static Machine createParallel(String id, String expression, SequenceFactory factoryParam, ParseDirection direction) {
		StateMachine stateMachine = new StateMachine(id, factoryParam);
		int i = A_ASCII; // A
		for (String subExpression : parseSubExpressions(expression)) {
			List<Expression> expressions = Expression.getExpressions(
					subExpression,
				  factoryParam.getSpecialStrings(),
				  factoryParam.getFormatterMode()
			);
			String prefix = String.valueOf((char) i);
			// Machine is built to have one shared start-state and one
			// end-state for *each* individual branch
			stateMachine.parseExpression(prefix, expressions, direction);
			i++;
		}
		return stateMachine;
	}

	private StateMachine() {
		this("E", SequenceFactory.getEmptyFactory());
	}

	private StateMachine(String id, SequenceFactory factoryParam) {
		factory = factoryParam;
		machineId = id;
		startStateId = machineId + ":S";

		machinesMap = new HashMap<>();
		acceptingStates = new HashSet<>();
		nodes = new HashSet<>();
		graph = new Graph();
	}
	
	public SequenceFactory getFactory() {
		return factory;
	}

	@Override
	public TreeSet<Integer> getMatchIndices(int startIndex, Sequence target) {
		TreeSet<Integer> indices = new TreeSet<>();

		if (graph.isEmpty()) {
			indices.add(0);
			return indices;
		}

		Sequence sequence = new BasicSequence(target);
		sequence.add(factory.getBorderSegment());
		// At the beginning of the process, we are in the start-state
		// so we find out what arcs leave the node.
		List<MatchState> states = new ArrayList<>();
		// Add an initial state at the beginning of the sequence
		states.add(new MatchState(startIndex, startStateId));
		// if the condition is empty, it will always match
		List<MatchState> swap = new ArrayList<>();
		while (!states.isEmpty()) {
			for (MatchState state : states) {

				String currentNode = state.getNode();
				int index = state.getIndex();
				// Check internal state machines
				Collection<Integer> matchIndices;
				if (machinesMap.containsKey(currentNode)) {
					matchIndices = machinesMap.get(currentNode).getMatchIndices(index, target);
				} else {
					matchIndices = new HashSet<>();
					matchIndices.add(index);
				}

				if (acceptingStates.contains(currentNode)) {
					indices.addAll(matchIndices);
				}

				if (graph.contains(currentNode)) {
					for (Integer matchIndex : matchIndices) {
						Collection<MatchState> matchStates = updateSwapStates(sequence, currentNode, matchIndex);
						swap.addAll(matchStates);
					}
				}
			}
			states = swap;
			swap = new ArrayList<>();
		}
		return indices;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof StateMachine)) { return false; }

		StateMachine that = (StateMachine) obj;
		boolean isEqualAccepting = acceptingStates.equals(that.acceptingStates);
		boolean isEqualFactory = factory.equals(that.factory);
		boolean isEqualGraph = graph.equals(that.graph);
		boolean isEqualIds = machineId.equals(that.machineId);
		boolean isEqualMachines = machinesMap.equals(that.machinesMap);

		return  isEqualAccepting &&
			isEqualFactory &&
			isEqualGraph &&
			isEqualIds &&
			isEqualMachines;
	}

	@Override
	public int hashCode() {
		int result = factory.hashCode();
		result = 31 * result + machineId.hashCode();
		result = 31 * result + startStateId.hashCode();
		result = 31 * result + acceptingStates.hashCode();
		result = 31 * result + nodes.hashCode();
		result = 31 * result + machinesMap.hashCode();
		result = 31 * result + graph.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "StandardMachine{" +
			"nodes: " + nodes.size() + ", " +
			"machines: " + machinesMap.size() +
			'}';
	}

	// package only access
	Graph getGraph() {
		return graph;
	}

	// package only access
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	Map<String, Machine> getMachinesMap() {
		// this needs to mutable:
		// see NegativeStateMachine.create(..)
		return machinesMap;
	}

	private void parseExpression(String branchPrefix,
	                             List<Expression> expressions,
	                             ParseDirection direction) {
		nodes.add(startStateId);

		if (direction == ParseDirection.BACKWARD) { Collections.reverse(expressions); }

		int nodeId = 0;
		String previousNode = startStateId;

		for (Iterator<Expression> it = expressions.iterator(); it.hasNext(); ) {
			Expression expression = it.next();

			nodeId++;

			String expr = expression.getExpression();
			String meta = expression.getMetacharacter();

			boolean negative = expression.isNegative();

			String currentNode = machineId + ':' + branchPrefix + nodeId;

			nodes.add(currentNode);

			if (negative) {
				Machine machine = NegativeStateMachine.create(currentNode, expr, factory, direction);
				machinesMap.put(currentNode, machine);
				String nextNode = currentNode + 'X';
				nodes.add(nextNode);
				previousNode = constructRecursiveNode(nextNode, previousNode, currentNode, meta);
			} else {
				if (expr.startsWith("(")) {
					if (!expr.contains(")")) {
						throw new ParseException("Unmatched parenthesis.", expr);
					}
					String substring = expr.substring(1, expr.length() - 1);
					Machine machine = create(currentNode, substring, factory, direction);
					machinesMap.put(currentNode, machine);

					String nextNode = currentNode + 'X';
					nodes.add(nextNode);
					previousNode = constructRecursiveNode(nextNode, previousNode, currentNode, meta);
				} else if (expr.startsWith("{")) {
					if (!expr.contains("}")) {
						throw new ParseException("Unmatched parenthesis.", expr);
					}
					String substring = expr.substring(1, expr.length() - 1); // Remove braces
					Machine machine = createParallel(currentNode, substring, factory, direction);
					machinesMap.put(currentNode, machine);

					String nextNode = currentNode + 'X';
					nodes.add(nextNode);
					previousNode = constructRecursiveNode(nextNode, previousNode, currentNode, meta);
				} else {
					previousNode = constructTerminalNode(previousNode, currentNode, expr, meta);
				}
			}
			if (!it.hasNext()) {
				acceptingStates.add(previousNode);
			}
		}
	}   

	private String constructRecursiveNode(String nextNode,
	                                      String previousNode, 
	                                      String machineNode,
	                                      String meta) {
		switch (meta) {
			case "?":
				graph.put(previousNode, BasicSequence.EMPTY, machineNode);
				graph.put(machineNode, BasicSequence.EMPTY, nextNode);
				graph.put(previousNode, BasicSequence.EMPTY, nextNode);
				break;
			case "*":
				graph.put(previousNode, BasicSequence.EMPTY, machineNode);
				graph.put(machineNode, BasicSequence.EMPTY, previousNode);
				graph.put(previousNode, BasicSequence.EMPTY, nextNode);
				break;
			case "+":
				graph.put(previousNode, BasicSequence.EMPTY, machineNode);
				graph.put(machineNode, BasicSequence.EMPTY, nextNode);
				graph.put(nextNode, BasicSequence.EMPTY, previousNode);
				break;
			default:
				graph.put(previousNode, BasicSequence.EMPTY, machineNode);
				graph.put(machineNode, BasicSequence.EMPTY, nextNode);
				break;
		}
		return nextNode;
	}

	private String constructTerminalNode(String previousNode, 
	                                     String currentNode, 
	                                     String exp, 
	                                     String meta) {
		String referenceNode;

		Sequence sequence = factory.getSequence(exp);

		switch (meta) {
			case "?":
				graph.put(previousNode, sequence, currentNode);
				graph.put(previousNode, BasicSequence.EMPTY, currentNode);
				referenceNode = currentNode;
				break;
			case "*":
				graph.put(previousNode, sequence, previousNode);
				graph.put(previousNode, BasicSequence.EMPTY, currentNode);
				referenceNode = currentNode;
				break;
			case "+":
				graph.put(previousNode, sequence, currentNode);
				graph.put(currentNode, BasicSequence.EMPTY, previousNode);
				referenceNode = currentNode;
				break;
			default:
				graph.put(previousNode, sequence, currentNode);
				referenceNode = currentNode;
				break;
		}
		return referenceNode;
	}

	private static Iterable<String> parseSubExpressions(String expressionParam) {
		Collection<String> subExpressions = new ArrayList<>();

		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < expressionParam.length(); i++) {
			char c = expressionParam.charAt(i);
			/*  */
			if (c == '{') {
				int index = getIndex(expressionParam, '{', '}', i);
				buffer.append(expressionParam.substring(i, index));
				i = index - 1;
			} else if (c == '(') {
				int index = getIndex(expressionParam, '(', ')', i);
				buffer.append(expressionParam.substring(i, index));
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

	private static int getIndex(CharSequence string, char left, char right, int startIndex) {
		int count = 1;
		int endIndex = -1;

		boolean matched = true;
		for (int i = startIndex + 1; i <= string.length() && matched; i++) {
			char ch = string.charAt(i);
			if (ch == right && count == 1) {
				matched = false;
				endIndex = i;
			} else if (ch == right) {
				count++;
			} else if (ch == left) {
				count--;
			}
		}
		return endIndex;
	}

	private Collection<MatchState> updateSwapStates(Sequence testSequence, String currentNode, int index) {
		Sequence tail = testSequence.subsequence(index);
		Collection<MatchState> states = new HashSet<>();
		Map<Sequence, Set<String>> map = graph.get(currentNode);
		for (Map.Entry<Sequence, Set<String>> entry : map.entrySet()) {
			Sequence key = entry.getKey();
			Set<String> value = entry.getValue();
			for (String nextNode : value) {
				if (Objects.equals(key, BasicSequence.EMPTY)) {
					states.add(new MatchState(index, nextNode));
				} else if (factory.hasVariable(key.toString())) {
					for (Sequence s : factory.getVariableValues(key.toString())) {
						if (tail.startsWith(s)) {
							states.add(new MatchState(index + s.size(), nextNode));
						}
					}
				} else if (key.equals(factory.getDotSequence())) {
					if (!tail.startsWith(factory.getBorderSegment())) {
						states.add(new MatchState(index + key.size(), nextNode));
					} // Else: . cannot match #
				} else if (tail.startsWith(key)) {
					// Should work for both cases which have the same behavior
					states.add(new MatchState(index + key.size(), nextNode));
				}
				// Else: the pattern fails to match
			}
		}
		return states;
	}

	private  static final class MatchState {

		private final int    index; // Where in the sequence the cursor is
		private final String node;  // What node the cursor is currently on

		private MatchState(Integer i, String n) {
			index = i;
			node = n;
		}

		public int getIndex() {
			return index;
		}

		public String getNode() {
			return node;
		}

		@Override
		public String toString() {
			return "[" + index + ',' + node + ']';
		}

		@Override
		public int hashCode() {
			return (13 + index) * (32 + node.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			if (!(obj instanceof MatchState)) { return false; }
			
			MatchState other = (MatchState) obj;
			return index == other.index &&
				node.equals(other.node);
		}
	}
}
