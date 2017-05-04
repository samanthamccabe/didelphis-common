package org.didelphis.common.language.machines;

import org.didelphis.common.language.machines.interfaces.StateMachine;
import org.didelphis.common.structures.tuples.Triple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samantha on 3/17/17.
 */
public final class Graphs {
	private Graphs() {
	}

	public static <T> String asGML(StateMachine<T> machine) {
		StringBuilder sb = new StringBuilder(0x1000);

		String name = machine.getId();
		
		sb.append("graph [\n");
		sb.append("\tdirected 1\n");
		sb.append("\tcomment \"")
				.append(name)
				.append("\"\n");
		sb.append("\tlabel \"")
				.append(name)
				.append("\"\n");

		Map<String, Integer> idToIndex = new HashMap<>();
		int nodeIndex = 1;

		populate(nodeIndex, machine, sb, idToIndex);

		sb.append(']');
		return sb.toString();
	}

	private static <T> int populatePrimary(
			int nodeIndex,
			StateMachine<T> machine,
			StringBuilder stringBuilder,
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
			}
		}
		return nodeIndex;
	}

	private static <T> int populate(int nodeIndex,
			StateMachine<T> machine,
			StringBuilder sb,
			Map<String, Integer> idToIndex) {
		Iterable<Graph<T>> graphs = machine.getGraphs().values();
		for (Graph<T> graph : graphs) {

			for (Triple<String, T, Collection<String>> triple : graph) {
				

			//			for (Map.Entry<String, Map<T, Collection<String>>> e1 : graph.entrySet()) {
				String sourceId = triple.getFirstElement();
				nodeIndex = addNode(sb, idToIndex, nodeIndex, sourceId);
//				for (Map.Entry<T, Collection<String>> e2 : e1.getValue().entrySet()) {
					T arc = triple.getSecondElement();
					for (String targetId : triple.getThirdElement()) {
						nodeIndex = addNode(sb, idToIndex, nodeIndex, targetId);
						sb.append("\tedge [\n");
						sb.append("\t\tsource ")
								.append(idToIndex.get(sourceId))
								.append('\n');
						sb.append("\t\ttarget ")
								.append(idToIndex.get(targetId))
								.append('\n');
						sb.append("\t\tlabel \"")
								.append(String.valueOf(arc))
								.append("\"\n");
						sb.append("\t]\n");
					}
				}
			}
		
		return populatePrimary(nodeIndex + 1, machine, sb, idToIndex);
	}

	private static int addNode(StringBuilder sb,
			Map<String, Integer> idToIndex, int nodeIndex, String nodeId) {
		if (!idToIndex.containsKey(nodeId)) {
			idToIndex.put(nodeId, nodeIndex);
			sb.append("\tnode  [\n");
			sb.append("\t\tid ")
					.append(nodeIndex)
					.append('\n');
			sb.append("\t\tlabel \"")
					.append(nodeId)
					.append("\"\n");
			sb.append("\t]\n");
			return nodeIndex + 1;
		}
		return nodeIndex;
	}
}
