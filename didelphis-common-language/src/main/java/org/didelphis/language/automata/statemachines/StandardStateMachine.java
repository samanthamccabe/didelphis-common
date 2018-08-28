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
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.Graph;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.matching.BasicMatch;
import org.didelphis.language.automata.matching.LanguageMatcher;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.parsing.LanguageParser;
import org.didelphis.structures.tuples.Tuple;
import org.didelphis.structures.tuples.Twin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Samantha Fiona McCabe
 * @date 3/7/2015
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class StandardStateMachine<S> implements StateMachine<S> {
	
	static int A_ASCII = 0x41; // dec 65 / 0x41

	LanguageParser<S> parser;
	LanguageMatcher<S> matcher;
	String id;
	String startStateId;
	Collection<String> acceptingStates;
	Map<String, StateMachine<S>> machinesMap;
	
	List<Tuple<String, String>> groups;
	List<String> groupOrdering;
	
	// {String (Node ID), Sequence (Arc)} --> String (Node ID)
	Graph<S> graph;

	private StandardStateMachine(
			String id,
			LanguageParser<S> parser,
			LanguageMatcher<S> matcher
	) {
		this.parser = parser;
		this.id = id;
		this.matcher = matcher;

		startStateId = this.id + "-S";
	
		machinesMap = new HashMap<>();
		acceptingStates = new HashSet<>();
		graph = new Graph<>();
		groups = new ArrayList<>();
		groupOrdering = new ArrayList<>();
	}

	@NonNull
	public static <T> StateMachine<T> create(
			@NonNull String id,
			@NonNull Expression expression,
			@NonNull LanguageParser<T> parser,
			@NonNull LanguageMatcher<T> matcher
	) {
		StandardStateMachine<T> machine = new StandardStateMachine<>(
				id,
				parser,
				matcher
		);
		machine.parse(expression);
		return machine;
	}

	private void parse(@NonNull Expression expression) {
		List<Expression> list = Collections.singletonList(expression);
		String startId = startStateId;
		groupOrdering.add(startId);
		String accepting = parseExpression(0, startId, "O", list);
		acceptingStates.add(accepting);
		groups.add(new Twin<>(startId, accepting));

		// Sort groups by the group-ordering
		List<Tuple<String,String>> orderedGroups = new ArrayList<>();
		for (String node : groupOrdering) {
			for (Tuple<String, String> group : groups) {
				if (node.equals(group.getLeft())) {
					orderedGroups.add(group);
				}
			}
		}

		groups.clear();
		groups.addAll(orderedGroups);
	}

	@NonNull
	@Override
	public LanguageParser<S> getParser() {
		return parser;
	}

	@NonNull
	@Override
	public LanguageMatcher<S> getMatcher() {
		return matcher;
	}

	@Override
	public String getId() {
		return id;
	}

	@NonNull
	@Override
	public Map<String, Graph<S>> getGraphs() {
		Map<String, Graph<S>> map = new HashMap<>();
		map.put(id, graph);
		return map;
	}

	@NonNull
	@Override
	@Contract ("_,_ -> new")
	public Match<S> match(@NonNull S input, int start) {

		if (graph.isEmpty()) {
			return new BasicMatch<>(input, 0, 0);
		}

		// Variables for managing capture groups
		Map<String, Integer> startNodes = new HashMap<>();
		Map<String, Integer> endNodes = new HashMap<>();

		for (int i = 0; i < groups.size(); i++) {
			Tuple<String, String> tuple = groups.get(i);
			startNodes.put(tuple.getLeft(), i);
			endNodes.put(tuple.getRight(), i);
		}

		List<Cursor> cursorSwap = new ArrayList<>();
		List<Cursor> cursorList = new ArrayList<>();
		// Start here
		cursorList.add(new Cursor(start, startStateId));

		Set<Match<S>> matches = new HashSet<>();
		while (!cursorList.isEmpty()) {
			for (Cursor cursor : cursorList) {

				String currentNode = cursor.getNode();
				int index = cursor.getIndex();

				if (machinesMap.containsKey(currentNode) && index >= 0) {
					StateMachine<S> machine = machinesMap.get(currentNode);
					Match<S> match = machine.match(input, index);
					int end = match.end();
					if (end >= 0) {
						cursor.setIndex(end);
					} else {
						continue;
					}
				}

				if (acceptingStates.contains(currentNode)) {
					int end = cursor.getIndex();

					S matchedInput = parser.subSequence(input, start, end);

					BasicMatch<S> match = new BasicMatch<>(matchedInput,
							start,
							end
					);

					for (int i = 0; i < groups.size(); i++) {
						int groupStart = cursor.getGroupStart(i);
						int groupEnd = cursor.getGroupEnd(i);
						S seq = parser.subSequence(input, groupStart, groupEnd);
						match.addGroup(groupStart, groupEnd, seq);
					}
					matches.add(match);
				}

				if (graph.containsKey(currentNode)) {
					if (cursor.getIndex() > parser.lengthOf(input)) {
						continue;
					}

					// update captures?
					if (startNodes.containsKey(currentNode)) {
						int group = startNodes.get(currentNode);
						if (cursor.getGroupStart(group) == -1) {
							cursor.setGroupStart(group, index);
						}
					}
					
					List<Cursor> cursors = checkNode(input, cursor);

					for (Cursor cursor1 : cursors) {
						String node = cursor1.getNode();
						if (endNodes.containsKey(node)) {
							int groupId = endNodes.get(node);
							cursor1.setGroupEnd(groupId, cursor.getIndex());
						}
					}
					
					cursorSwap.addAll(cursors);
				}
			}
			cursorList = cursorSwap;
			cursorSwap = new ArrayList<>();
		}

		Optional<Match<S>> matchEnd = matches.stream()
				.max(Comparator.comparingInt(Match::end));

		return matchEnd.orElseGet(() -> new BasicMatch<>(input, -1, -1));
	}

	@Override
	public String toString() {
		return "StandardStateMachine{" + id + '}';
	}

	@NonNull
	public Map<String, StateMachine<S>> getMachinesMap() {
		// this needs to mutable:
		// see NegativeStateMachine.create(..)
		return machinesMap;
	}

	/**
	 * Evaluates the inputs against the arcs leaving the current node and if
	 * successful, will add to the output indices
	 *
	 * @param input       the input data being consumed by this automaton
	 * @param cursor
	 *
	 * @return a collection of new states
	 */
	private List<Cursor> checkNode(
			@NonNull S input,
			@NonNull Cursor cursor
	) {
		String currentNode = cursor.getNode();
		int index = cursor.getIndex();

		List<Cursor> cursors = new ArrayList<>();
		Map<S, Collection<String>> map = graph.get(currentNode);
		for (Entry<S, Collection<String>> entry : map.entrySet()) {
			S arc = entry.getKey();
			Collection<String> value = entry.getValue();
			for (String node : value) {
				if (eq(arc, parser.epsilon())) {
					cursors.add(new Cursor(index, node));
				} else if (eq(arc, parser.getDot()) && parser.lengthOf(input) > 0) {
					cursors.add(new Cursor(index + 1, node));
				} else if (eq(arc, parser.getWordStart()) && index == 0) {
					cursors.add(new Cursor(0, node));
				} else if (eq(arc, parser.getWordEnd()) && index == parser.lengthOf(input)) {
					cursors.add(new Cursor(index, node));
				} else {
					int matchLength = matcher.matches(input, arc, index);
					if (matchLength >= 0) {
						cursors.add(new Cursor(index + matchLength, node));
					}
				}
			}
		}

		for (Cursor cursor1 : cursors) {
			for (int i = 0; i < groups.size(); i++) {
				cursor1.setGroupStart(i, cursor.getGroupStart(i));
				cursor1.setGroupEnd(i, cursor.getGroupEnd(i));
			}
		}
		
		return cursors;
	}

	private boolean eq(S arc, S dot) {
		return Objects.equals(arc, dot);
	}

	/**
	 * 
	 * @param startingIndex
	 * @param startNode
	 * @param prefix
	 * @param expressions
	 * @return the current node id
	 */
	private String parseExpression(
			int startingIndex,
			@NonNull String startNode,
			@NonNull String prefix,
			@NonNull Iterable<Expression> expressions
	) {
		int nodeId = startingIndex;
		String previous = startNode;
		for (Expression expression : expressions) {
			nodeId++;
			
			String meta = expression.getQuantifier();
			boolean negative = expression.isNegative();
			
			String current = prefix + '-' + nodeId;

			if (expression.isCapturing()) {
				groupOrdering.add(current);
			}
			
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
						String node = makeParallel(current, nodeId, expression);
						previous = makeRecursiveNode(node, current, meta);
					} else {
						graph.add(previous, parser.epsilon(), current);
						String endNode = parseExpression(nodeId,
								current,
								"G-" + current,
								expression.getChildren());
						previous = makeRecursiveNode(endNode, current, meta);
						
						if (expression.isCapturing()) {
							groups.add(new Twin<>(current, endNode));
						}
					}	
				} else {
					previous = makeTerminalNode(previous,
							current,
							expression.getTerminal(),
							meta);
				}
			}
		}
		return previous;
	}

	@NonNull
	private String makeParallel(
			@NonNull String start, int index, @NonNull Expression expression
	) {
		int i = A_ASCII; // A
		String output = start + "-Out";
		for (Expression child : expression.getChildren()) {
			String prefix = String.valueOf((char) i);
			// Machine is built to have one shared start-state and one end-state
			// for *each* individual branch
			String closingState = parseExpression(index,
					start,
					prefix + '-' + start,
					Collections.singletonList(child)
			);
			i++;
			graph.add(closingState, parser.epsilon(), output);
		}
		return output;
	}


	private void createNegative(
			@NonNull Expression expression,
			@NonNull String current
	) {
		Expression negated = expression.withNegative(false).withQuantifier("");
		StateMachine<S> machine = NegativeStateMachine.create(current,
				negated,
				parser,
				matcher);
		machinesMap.put(current, machine);
	}

	private String constructNegativeNode(
			@NonNull String end,
			@NonNull String start, 
			@NonNull String machine, 
			@NonNull String meta
	) {
		graph.add(start, parser.epsilon(), machine);
		addToGraph(start, end, machine, meta);
		return end;
	}

	private void addToGraph(
			@NotNull String start,
			@NotNull String end,
			@NotNull String machine,
			@NotNull String meta
	) {
		S e = parser.epsilon();
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
	private String makeRecursiveNode(
			@NonNull String machineNode, 
			@NonNull String startNode, 
			@NonNull String meta
	) {
		String endNode = startNode + 'X';
		addToGraph(startNode, endNode, machineNode, meta);
		return endNode;
	}

	@NonNull
	private String makeTerminalNode(
			@NonNull String previousNode,
			@NonNull String currentNode, 
			@NonNull String exp, 
			@NonNull String meta) {
		S t = parser.transform(exp);
		S e = parser.epsilon();
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

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	private final class Cursor {

		int index;
		String node;

		final int[] groupStart;
		final int[] groupEnd ;
		
		private Cursor(int index, String node) {
			this.index = index;
			this.node = node;
			
			groupStart = new int[groups.size()];
			groupEnd   = new int[groups.size()];

			for (int i = 0; i < groups.size(); i++) {
				groupStart[i] = -1;
				groupEnd[i]   = -1;
			}
		}
		
		private void setGroupStart(int group, int index) {
			groupStart[group] = index;
		}

		private void setGroupEnd(int group, int index) {
			groupEnd[group] = index;
		}
		
		private int getGroupStart(int group) {
			return groupStart[group];
		}
		
		private int getGroupEnd(int group) {
			return groupEnd[group];
		}
	}
}
