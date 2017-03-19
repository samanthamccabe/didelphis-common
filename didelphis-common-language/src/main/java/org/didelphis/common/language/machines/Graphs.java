package org.didelphis.common.language.machines;

import org.didelphis.common.language.machines.interfaces.StateMachine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by samantha on 3/17/17.
 */
public final class Graphs {
	private Graphs() {
	}

	public static <T> String asGML(StateMachine<T> machine) {
		StringBuilder stringBuilder = new StringBuilder(0x1000);

		String name = machine.getId();
		
		stringBuilder.append("graph [\n");
		stringBuilder.append("\tdirected 1\n");
		stringBuilder.append("\tcomment \"")
				.append(name)
				.append("\"\n");
		stringBuilder.append("\tlabel \"")
				.append(name)
				.append("\"\n");

		Map<String, Integer> idToIndex = new HashMap<>();
		int nodeIndex = 1;

		nodeIndex = populatePrimary(nodeIndex,
				machine,
				stringBuilder,
				idToIndex);

		populate(nodeIndex, machine, stringBuilder, idToIndex);

		stringBuilder.append(']');
		return stringBuilder.toString();
	}

	private static <T> int populatePrimary(int nodeIndex,
			StateMachine<T> machine, StringBuilder stringBuilder,
			Map<String, Integer> idToIndex) {
		if (machine instanceof StandardStateMachine) {
			Iterable<StateMachine<T>> values = ((StandardStateMachine<T>) machine)
					.getMachinesMap()
					.values();
			for (StateMachine<T> stateMachine : values) {
				nodeIndex = populate(nodeIndex,
						stateMachine,
						stringBuilder,
						idToIndex);
				if (stateMachine instanceof StandardStateMachine) {
					Iterable<StateMachine<T>> machines = ((StandardStateMachine<T>) stateMachine)
							.getMachinesMap()
							.values();
					for (StateMachine<T> subMachine : machines) {
						nodeIndex = populate(nodeIndex,
								subMachine,
								stringBuilder,
								idToIndex);
					}
				}
			}
		}
		return nodeIndex;
	}

	private static <T> int populate(int nodeIndex,
			StateMachine<T> machine,
			StringBuilder stringBuilder,
			Map<String, Integer> idToIndex) {
		Collection<Graph<T>> graphs = machine.getGraphs().values();
		for (Graph<T> graph : graphs) {
			for (Map.Entry<String, Map<T, Set<String>>> e1 : graph.entrySet()) {
				String sourceId = e1.getKey();
				nodeIndex = addNode(stringBuilder, idToIndex, nodeIndex, sourceId);
				for (Map.Entry<T, Set<String>> e2 : e1.getValue().entrySet()) {
					T arc = e2.getKey();
					for (String targetId : e2.getValue()) {
						nodeIndex = addNode(stringBuilder, idToIndex, nodeIndex, targetId);
						stringBuilder.append("\tedge [\n");
						stringBuilder.append("\t\tsource ")
								.append(idToIndex.get(sourceId))
								.append('\n');
						stringBuilder.append("\t\ttarget ")
								.append(idToIndex.get(targetId))
								.append('\n');
						stringBuilder.append("\t\tlabel \"")
								.append(String.valueOf(arc))
								.append("\"\n");
						stringBuilder.append("\t]\n");
					}
				}
			}
		}
		return populatePrimary(nodeIndex + 1, machine, stringBuilder, idToIndex);
	}

	private static int addNode(StringBuilder stringBuilder,
			Map<String, Integer> idToIndex, int nodeIndex, String nodeId) {
		if (!idToIndex.containsKey(nodeId)) {
			idToIndex.put(nodeId, nodeIndex);
			stringBuilder.append("\tnode  [\n");
			stringBuilder.append("\t\tid ")
					.append(nodeIndex)
					.append('\n');
			stringBuilder.append("\t\tlabel \"")
					.append(nodeId)
					.append("\"\n");
			stringBuilder.append("\t]\n");
			return nodeIndex + 1;
		}
		return nodeIndex;
	}
}
