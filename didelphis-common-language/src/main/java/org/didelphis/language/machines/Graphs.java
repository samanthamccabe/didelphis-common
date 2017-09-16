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

package org.didelphis.language.machines;

import lombok.NonNull;
import org.didelphis.language.machines.interfaces.StateMachine;
import org.didelphis.structures.tuples.Triple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samantha on 3/17/17.
 */
public final class Graphs {
	private Graphs() {
	}

	public static <T> String asGML(@NonNull StateMachine<T> machine) {
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
			@NonNull StringBuilder stringBuilder,
			@NonNull Map<String, Integer> idToIndex) {
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
			@NonNull StateMachine<T> machine,
			@NonNull StringBuilder sb,
			@NonNull Map<String, Integer> idToIndex) {
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

	private static int addNode(@NonNull StringBuilder sb,
			@NonNull Map<String, Integer> idToIndex, int nodeIndex, String nodeId) {
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
