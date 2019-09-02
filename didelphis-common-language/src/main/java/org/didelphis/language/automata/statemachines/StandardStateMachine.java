/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.automata.statemachines;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.didelphis.language.automata.expressions.Expression;
import org.didelphis.language.automata.matching.BasicMatch;
import org.didelphis.language.automata.matching.Match;
import org.didelphis.language.automata.parsing.LanguageParser;
import org.didelphis.structures.graph.Arc;
import org.didelphis.structures.graph.Graph;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.structures.tuples.Tuple;
import org.didelphis.structures.tuples.Twin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class StandardStateMachine<S> implements StateMachine<S> {
	
	LanguageParser<S> parser;
	
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
			@NonNull String expression,
			@NonNull LanguageParser<T> parser) {
		//noinspection IfMayBeConditional
		if (expression.isEmpty()) {
			return new EmptyMachine<>(id, parser);
		} else {
			return new StandardStateMachine<>(id,
					parser.parseExpression(expression),
					parser);
		}
	}
	
	@NonNull
	public static <T> StateMachine<T> create(
			@NonNull String id,
			@NonNull Expression expression,
			@NonNull LanguageParser<T> parser) {
		//noinspection IfMayBeConditional
		if (!expression.hasChildren() && expression.getTerminal().isEmpty()) {
			return new EmptyMachine<>(id, parser);
		} else {
			return new StandardStateMachine<>(id, expression, parser);
		}
	}
	
	private StandardStateMachine(
			@NonNull String id,
			@NonNull Expression expression,
			@NonNull LanguageParser<S> parser
	) {
		this.id = id;
		this.parser = parser;

		startStateId = this.id + "-S";
	
		machinesMap = new HashMap<>();
		acceptingStates = new HashSet<>();
		graph = new Graph<>();
		
		// build machine
		List<Expression> captures = new ArrayList<>();
		captures.add(null); // null element is a placeholder for group zero
		populateCaptures(expression, captures);

		groups = new ArrayList<>(captures.size());
		for (int i = 0; i < captures.size(); i++) {
			groups.add(null);
		}

		List<Expression> list = Collections.singletonList(expression);
		String state = parse(0, startStateId, "Z", list, captures);
		acceptingStates.add(state);
	}

	private StandardStateMachine(
			@NonNull String id,
			@NonNull Expression expression,
			@NonNull LanguageParser<S> parser,
			@NonNull List<Expression> captures
	) {
		this.id = id;
		this.parser = parser;

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
		String endState = parse(0, startStateId, "Z", list, captures);
		acceptingStates.add(endState);
	}

	@NonNull
	@Override
	public LanguageParser<S> getParser() {
		return parser;
	}

	@NonNull
	@Override
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
		cursorList.add(new Cursor(start, startStateId, groups.size()));

		List<Match<S>> matches = new ArrayList<>();
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
						for (int i = 1; i < match.groupCount(); i++) {
							int start1 = match.start(i);
							int end1 = match.end(i);
							// don't overwrite existing groups with non-matched
							if (start1 >= 0 && end1 >= 0) {
								cursor.setGroupStart(i, start1);
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
							int group = endNodes.get(node);
							if (cursor.getGroupEnd(group) == -1) {
								int end = aCursor.getIndex();
								aCursor.setGroupEnd(group, end);
							}
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

	@Override
	public String toString() {
		return "StandardStateMachine{" + id + '}';
	}

	@Override
	public String getId() {
		return id;
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
		String currentNode = cursor.getNode();
		int index = cursor.getIndex();

		List<Cursor> cursors = new ArrayList<>();
		Map<Arc<S>, Collection<String>> map = graph.get(currentNode);
		for (Entry<Arc<S>, Collection<String>> entry : map.entrySet()) {
			Arc<S> arc = entry.getKey();
			Collection<String> value = entry.getValue();
			for (String node : value) {
				int newIndex = arc.match(input, index);
				if (newIndex >= 0) {
					cursors.add(new Cursor(newIndex, node, groups.size()));
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
	private String parse(
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
			String current = prefix + '-' + nodeId;

			if (expression.isNegative()) {
				createNegative(expression, current, captures);
				String nextNode = current + 'X';
				previous = makeNegative(previous, nextNode, current, meta);
				if (captures.contains(expression)) {
					int index = captures.indexOf(expression);
					groups.set(index, new Twin<>(current, previous));
				}
				continue;
			}

			graph.add(previous, parser.epsilon(), current);
			if (expression.hasChildren()) {
				if (expression.isParallel()) {
					String node = makeParallel(nodeId,
							current,
							expression,
							captures
					);
					previous = makeGroup(current, node, meta);
				} else {
					List<Expression> children = expression.getChildren();
					String node = parse(nodeId,
							current,
							"G-" + current,
							children,
							captures
					);
					previous = makeGroup(current, node, meta);
					if (captures.contains(expression)) {
						int index = captures.indexOf(expression);
						groups.set(index, new Twin<>(current, previous));
					}
				}
			} else { 
				String terminal = expression.getTerminal();
				previous = makeTerminal(current, "T-"+current, terminal, meta);
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
			String closingState = parse(
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
			@NonNull String machineNode,
			@NonNull List<Expression> captures
	) {
		Expression negated = expression.withNegative(false).withQuantifier("");
		StateMachine<S> stateMachine = NegativeMachine.create(machineNode,
				negated,
				parser,
				captures);
		machinesMap.put(machineNode, stateMachine);
	}

	private String makeNegative(
			@NonNull String start,
			@NonNull String end,
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
		Arc<S> epsilon = parser.epsilon();
		switch (meta) {
			case "?":
				graph.add(machine, epsilon, end);
				graph.add(start,   epsilon, end);
				break;
			case "*":
				graph.add(machine, epsilon, start);
				graph.add(start,   epsilon, end);
				break;
			case "+":
				graph.add(machine, epsilon, end);
				graph.add(end,     epsilon, start);
				break;
			default:
				graph.add(machine, epsilon, end);
				break;
		}
	}

	@NonNull
	private String makeGroup(
			@NonNull String start,
			@NonNull String machine, 
			@NonNull String meta
	) {
		String endNode = start + 'X';
		addToGraph(start, endNode, machine, meta);
		return endNode;
	}

	@NonNull
	private String makeTerminal(
			@NonNull String start,
			@NonNull String end,
			@NonNull String exp,
			@NonNull String meta
	) {
		Arc<S> sequence = parser.getArc(exp);
		Arc<S> epsilon = parser.epsilon();
		switch (meta) {
			case "?":
				graph.add(start, sequence, end);
				graph.add(start, epsilon,  end);
				break;
			case "*":
				graph.add(start, sequence, start);
				graph.add(start, epsilon,  end);
				break;
			case "+":
				graph.add(start, sequence, end);
				graph.add(end,   epsilon,  start);
				break;
			default:
				graph.add(start, sequence, end);
		}
		return end;
	}

	private static void populateCaptures(
			@NonNull Expression expression, 
			@NonNull List<Expression> captures
	) {
		if (expression.isCapturing()) {
			captures.add(expression);
		}
		if (expression.hasChildren()) {
			for (Expression child : expression.getChildren()) {
				populateCaptures(child, captures);
			}
		}
	}

	@EqualsAndHashCode
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	private static final class NegativeMachine<S> implements StateMachine<S> {
	
		String id;
		StateMachine<S> negative;
		StateMachine<S> positive;
	
		private NegativeMachine(
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
				@NonNull List<Expression> captures
		) {
			// Create the actual branch, the one we don't want to match
			StateMachine<S> negative = new StandardStateMachine<>('N' + id,
					expression,
					parser,
					captures
			);

			StateMachine<S> positive = new StandardStateMachine<>('P' + id,
					replaceDots(expression, parser),
					parser,
					captures
			);
			return new NegativeMachine<>(id, negative, positive);
		}

		private static <S> Expression replaceDots(
				Expression expression, 
				LanguageParser<S> parser
		) {
			if (expression.hasChildren()) {
				List<Expression> children = expression.getChildren();
				for (int i = 0; i < children.size(); i++) {
					Expression expression2 = children.get(i);
					children.set(i, replaceDots(expression2, parser));
				}
			} else {
				String terminal = expression.getTerminal();
				MultiMap<String, S> specialsMap = parser.getSpecialsMap();
				if (specialsMap.containsKey(terminal)) {
					Collection<S> collection = specialsMap.get(terminal);

					int min = Integer.MAX_VALUE;
					int max = Integer.MIN_VALUE;
					for (S sequence : collection) {
						int length = parser.lengthOf(sequence);
						if (length < min) {
							min = length;
						}
						if (length > max) {
							max = length;
						}
					}
					
					StringBuilder sb = new StringBuilder();

					for (int i = 0; i < min; i++) {
						sb.append('.');
					}
					for (int i = 0; i < max - min; i++) {
						sb.append(".?");
					}

					return parser.parseExpression(sb.toString());

				} else {
					return expression.withTerminal(".");
				}
			}
			return expression;
		}
		
		@NonNull
		@Override
		public LanguageParser<S> getParser() {
			return positive.getParser();
		}

		@NonNull
		@Override
		public String getId() {
			return id;
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
	}

	@ToString
	@EqualsAndHashCode
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	private static final class EmptyMachine<S> implements StateMachine<S> {

		// The wildcard-extends is necessary to avoid an incompatible type error
		// even though Graph itself cannot be extended
		Map<String, ? extends Graph<?>> map = Collections.emptyMap();

		String id;
		LanguageParser<S> parser;

		private EmptyMachine(
				@NonNull String id, 
				@NonNull LanguageParser<S> parser
		) {
			this.id = id;
			this.parser = parser;
		}

		@NonNull
		@Override
		public LanguageParser<S> getParser() {
			return parser;
		}

		@Override
		public String getId() {
			return id;
		}
		
		@NonNull
		@Override
		public Match<S> match(@NonNull S input, int start) {
			return new BasicMatch<>(input, start, start);
		}

		@NonNull
		@Override
		public List<S> split(@NonNull S input, int limit) {
			List<S> list = new ArrayList<>();
			for (int i = 0; i < parser.lengthOf(input); i++) {
				list.add(parser.subSequence(input, i, i + 1));
			}
			return list;
		}

		@NonNull
		@Override
		public S replace(@NonNull S input, @NonNull S replacement) {
			int size = parser.lengthOf(input);
			S sequence = parser.transform("");
			for (int i = 0; i < size; i++) {
				S q = parser.subSequence(input, i, i + 1);
				sequence = parser.concatenate(sequence, q);
				if (i < size - 1) {
					sequence = parser.concatenate(sequence, replacement);
				}
			}
			return sequence;
		}
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	private static final class Cursor {

		final int[] groupStart;
		final int[] groupEnd ;
		int index;
		String node;

		private Cursor(int index, String node, int size) {
			this.index = index;
			this.node = node;

			groupStart = new int[size];
			groupEnd   = new int[size];

			for (int i = 0; i < size; i++) {
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
