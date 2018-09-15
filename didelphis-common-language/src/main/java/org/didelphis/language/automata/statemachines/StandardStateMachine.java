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
import org.didelphis.structures.tuples.Triple;
import org.didelphis.structures.tuples.Tuple;
import org.didelphis.structures.tuples.Twin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Samantha Fiona McCabe
 * @date 3/7/2015
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class StandardStateMachine<S> implements StateMachine<S> {
	
	LanguageParser<S> parser;
	LanguageMatcher<S> matcher;
	
	String id;
	String startStateId;
	Collection<String> acceptingStates;
	Map<String, StateMachine<S>> machinesMap;
	
	List<Tuple<String, String>> groups;
	
	// {String (Node ID), Sequence (Arc)} --> String (Node ID)
	Graph<S> graph;
	
	@NonNull
	public static <T> StateMachine<T> create(
			@NonNull String id,
			@NonNull Expression expression,
			@NonNull LanguageParser<T> parser,
			@NonNull LanguageMatcher<T> matcher
	) {
		return new StandardStateMachine<>(id, parser, matcher, expression);
	}

	public static <S> StateMachine<S> create(
			@NonNull String id,
			@NonNull Expression expression,
			@NonNull LanguageParser<S> parser,
			@NonNull LanguageMatcher<S> matcher,
			@NonNull List<Expression> captures
	) {
		return new StandardStateMachine<>(id, parser, matcher, expression, captures);
	}

	private StandardStateMachine(
			@NonNull String id,
			@NonNull LanguageParser<S> parser,
			@NonNull LanguageMatcher<S> matcher,
			@NonNull Expression expression
	) {
		this.id = id;
		this.parser = parser;
		this.matcher = matcher;

		startStateId = this.id + "-S";
	
		machinesMap = new HashMap<>();
		acceptingStates = new HashSet<>();
		graph = new Graph<>();
		
		// build machine
		List<Expression> captures = new ArrayList<>();
		captures.add(null); // null element is a placeholder for group zero
		getCaptureGroups(expression, captures);

		groups = new ArrayList<>(captures.size());
		for (int i = 0; i < captures.size(); i++) {
			groups.add(null);
		}

		List<Expression> list = Collections.singletonList(expression);
		String state = parseExpression(0, startStateId, "Z", list, captures);
		acceptingStates.add(state);
	}

	private StandardStateMachine(
			@NonNull String id,
			@NonNull LanguageParser<S> parser,
			@NonNull LanguageMatcher<S> matcher,
			@NonNull Expression expression,
			@NonNull List<Expression> captures
	) {
		this.id = id;
		this.parser = parser;
		this.matcher = matcher;

		startStateId = this.id + "-S";

		machinesMap = new HashMap<>();
		acceptingStates = new HashSet<>();
		graph = new Graph<>();

		// build machine
		groups = new ArrayList<>(captures.size());
		for (int i = 0; i < captures.size(); i++) {
			groups.add(null);
		}

		List<Expression> list = Collections.singletonList(expression);
		String accepting = parseExpression(0, startStateId, "Z", list, captures);
		acceptingStates.add(accepting);
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

	@Override
	@NonNull
	public Map<String, StateMachine<S>> getStateMachines() {
		// this needs to mutable:
		// see NegativeStateMachine.create(..)
		return machinesMap;
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
			if (tuple != null) {
				startNodes.put(tuple.getLeft(), i);
				endNodes.put(tuple.getRight(), i);
			}
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
						// copy valid groups from the match to the cursor
						for (int i = 0; i < match.groupCount(); i++) {
							int start1 = match.start(i);
							cursor.setGroupStart(i, start1);
							int end1 = match.end(i);
							// don't overwrite existing groups with non-matched
							if (start1 >= 0 && end1 >= 0) {
								cursor.setGroupEnd(i, end1);
							}
						}
					} else {
						continue;
					}
				}

				if (acceptingStates.contains(currentNode)) {
					int end = cursor.getIndex();
					S seq = parser.subSequence(input, start, end);
					BasicMatch<S> match = new BasicMatch<>(seq, start, end);
					
					// Add special group zero
					match.addGroup(0, end, seq);
					
					for (int i = 1; i < groups.size(); i++) {
						int gStart = cursor.getGroupStart(i);
						int gEnd = cursor.getGroupEnd(i);
						if (gStart < 0 || gEnd < 0 || (gStart == gEnd)) {
							match.addGroup(-1, -1, null);
						} else {
							S sub = parser.subSequence(input, gStart, gEnd);
							match.addGroup(gStart, gEnd, sub);
						}
					}
					matches.add(match);
				}

				// This check is done because not all nodes are keys in the
				// graph; if a node is terminal, with no outgoing arcs, then
				// the node will not be a key in the graph
				if (graph.containsKey(currentNode)) {
					if (cursor.getIndex() > parser.lengthOf(input)) {
						continue;
					}

					// update captures
					if (startNodes.containsKey(currentNode)) {
						int group = startNodes.get(currentNode);
						if (cursor.getGroupStart(group) == -1) {
							cursor.setGroupStart(group, index);
						}
					}
					
					List<Cursor> cursors = checkNode(input, cursor);
					for (Cursor aCursor : cursors) {
						String node = aCursor.getNode();
						if (endNodes.containsKey(node)) {
							int groupId = endNodes.get(node);
							int end = aCursor.getIndex();
							aCursor.setGroupEnd(groupId, end);
						}
					}
					cursorSwap.addAll(cursors);
				}
			}
			cursorList = cursorSwap;
			cursorSwap = new ArrayList<>();
		}
		
		Match<S> best = BasicMatch.empty(groups.size());
		for (Match<S> match : matches) {
			if (match.end() > best.end()) {
				best = match;
			}
		}
		return best;
	}

	@NonNull
	@Override
	public List<S> split(@NonNull S input, int limit) {
		int index = 0;
		boolean matchLimited = limit > 0;
		List<S> matchList = new ArrayList<>();

		int i = 0;
		int length = parser.lengthOf(input);
		while (i < length) {
			Match<S> m = match(input, i);

			if (!matchLimited || matchList.size() < limit - 1) {
				if (index == 0 && index == m.start() && m.start() == m.end()) {
					// no empty leading substring included for zero-width match
					// at the beginning of the input char sequence.
					continue;
				}
				S match = parser.subSequence(input, index, m.start());
				matchList.add(match);
				index = m.end();
			} else if (matchList.size() == limit - 1) { // last one
				S match = parser.subSequence(input, index,
						parser.lengthOf(input));
				matchList.add(match);
				index = m.end();
			}
			i++;
		}
		
		// If no match was found, return this
		if (index == 0) {
			return Collections.singletonList(input);
		}
		
		// Add remaining segment
		if (!matchLimited || matchList.size() < limit) {
			matchList.add(parser.subSequence(input, index, length));
		}

		// Construct result
		int resultSize = matchList.size();
		if (limit == 0) {
			while (resultSize > 0 && matchList.get(resultSize - 1).equals("")) {
				resultSize--;
			}
		}
		return matchList.subList(0, resultSize);
	}

	@NonNull
	@Override
	public S replace(@NonNull S input, @NonNull S replacement) {
		return null; //  TODO:
	}

	@Override
	public String toString() {
		return "StandardStateMachine{" + id + '}';
	}

	/**
	 * Evaluates the inputs against the arcs leaving the current node and if
	 * successful, will add to the output indices
	 *
	 * @param input the input data being consumed by this automaton
	 * @param cursor a {@link Cursor} object which store the current node and
	 * 		its associated position in the input
	 *
	 * @return a collection of new states
	 */
	@NonNull
	private List<Cursor> checkNode(
			@NonNull S input,
			@NonNull Cursor cursor
	) {
		int length = parser.lengthOf(input);
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
					continue;
				}
				
				if (eq(arc, parser.getDot()) && length > 0 && index < length) {
					cursors.add(new Cursor(index + 1, node));
				} else if (eq(arc, parser.getWordStart()) && index == 0) {
					cursors.add(new Cursor(0, node));
				} else if (eq(arc, parser.getWordEnd()) && index == length) {
					cursors.add(new Cursor(index, node));
				} else {
					int matchLength = matcher.matches(input, arc, index);
					if (matchLength >= 0) {
						cursors.add(new Cursor(index + matchLength, node));
					}
				}
			}
		}

		for (Cursor aCursor : cursors) {
			for (int i = 0; i < groups.size(); i++) {
				aCursor.setGroupStart(i, cursor.getGroupStart(i));
				aCursor.setGroupEnd(i, cursor.getGroupEnd(i));
			}
		}
		
		return cursors;
	}

	/**
	 * The primary parse function converting an {@link Expression} tree into the
	 * corresponding state machine.
	 *
	 * @param startingIndex a numerical index value used in generating
	 * 		sequential state ids
	 * @param startNode the node to which additional nodes will be attached
	 * @param prefix a prefix used in the creation of new nodes
	 * @param expressions the {@link Expression} tree from which the new
	 * 		states will be derived
	 * @param captures the list of {@link Expression}s which are capturing;
	 * 		this is used to assign start and end nodes to capture groups
	 *
	 * @return the current node id, <emph>i.e.</emph> the id of the most
	 * 		recently created node
	 */
	@NonNull
	private String parseExpression(
			int startingIndex,
			@NonNull String startNode,
			@NonNull String prefix,
			@NonNull Iterable<Expression> expressions, 
			@NonNull List<Expression> captures
	) {
		int nodeId = startingIndex;
		String previous = startNode;
		for (Expression expression : expressions) {
			nodeId++;

			String meta = expression.getQuantifier();
			boolean negative = expression.isNegative();

			String current = prefix + '-' + nodeId;

			if (negative) {
				createNegative(expression, current, captures);
				String nextNode = current + 'X';
				previous = constructNegativeNode(nextNode,
						previous,
						current,
						meta
				);
				if (captures.contains(expression)) {
					int index = captures.indexOf(expression);
					groups.set(index, new Twin<>(current, nextNode));
				}
			} else {
				if (expression.hasChildren()) {
					if (expression.isParallel()) {
						graph.add(previous, parser.epsilon(), current);
						String node = makeParallel(nodeId, current, expression,
								captures
						);
						previous = makeRecursiveNode(node, current, meta);
					} else {
						graph.add(previous, parser.epsilon(), current);
						String endNode = parseExpression(
								nodeId,
								current,
								"G-" + current,
								expression.getChildren(),
								captures
						);
						previous = makeRecursiveNode(endNode, current, meta);

						if (captures.contains(expression)) {
							int index = captures.indexOf(expression);
							groups.set(index, new Twin<>(current, endNode));
						}
					}
				} else {
					previous = makeTerminalNode(previous,
							current,
							expression.getTerminal(),
							meta
					);
				}
			}
		}
		return previous;
	}

	@NonNull
	private String makeParallel(
			int index,
			@NonNull String start,
			@NonNull Expression expression,
			@NonNull List<Expression> captures
	) {
		int i = 0;
		String output = start + "-Out";
		for (Expression child : expression.getChildren()) {
			String prefix = "P" + i;
			// Machine is built to have one shared start-state and one end-state
			// for *each* individual branch
			String closingState = parseExpression(
					index,
					start,
					prefix + '-' + start,
					Collections.singletonList(child),
					captures
			);
			i++;
			graph.add(closingState, parser.epsilon(), output);
		}
		return output;
	}

	private void createNegative(
			@NonNull Expression expression,
			@NonNull String current,
			@NonNull List<Expression> captures
	) {
		Expression negated = expression.withNegative(false).withQuantifier("");
		StateMachine<S> machine = NegativeStateMachine.create(current,
				negated,
				parser,
				matcher,
				captures);
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
			@NonNull String meta
	) {
		S t = parser.transform(exp);
		S e = parser.epsilon();
		switch (meta) {
			case "?":
				graph.add(previousNode, t, currentNode);
				graph.add(previousNode, e, currentNode);
				return currentNode;
			case "*":
				graph.add(previousNode, t, previousNode);
				graph.add(previousNode, e, currentNode);
				return currentNode;
			case "+":
				graph.add(previousNode, t, currentNode);
				graph.add(currentNode, e, previousNode);
				return currentNode;
			default:
				graph.add(previousNode, t, currentNode);
				return currentNode;
		}
	}

	private static void getCaptureGroups(
			@NonNull Expression expression, 
			@NonNull List<Expression> captures
	) {
		if (expression.isCapturing()) {
			captures.add(expression);
		}
		if (expression.hasChildren()) {
			for (Expression child : expression.getChildren()) {
				getCaptureGroups(child, captures);
			}
		}
	}

	private static <S> boolean eq(S arc, S dot) {
		return Objects.equals(arc, dot);
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	private final class Cursor {

		final int[] groupStart;
		final int[] groupEnd ;
		int index;
		String node;
		
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
	
	private static final class NegativeStateMachine<S> implements StateMachine<S> {
	
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
		private static <S> StateMachine<S> create(
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
	
		@NonNull
		@Override
		public List<S> split(@NonNull S input, int limit) {
			throw new UnsupportedOperationException("method #split is not supported by this implementation");
		}
	
		@NonNull
		@Override
		public S replace(@NonNull S input, @NonNull S replacement) {
			throw new UnsupportedOperationException("method #replace is not supported by this implementation");
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
}
