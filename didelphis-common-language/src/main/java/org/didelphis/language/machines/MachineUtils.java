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

import java.util.Map.Entry;

/**
 * Created by samantha on 3/14/17.
 */
public final class MachineUtils {
	
	private MachineUtils() {}

	private <T> void generate(@NonNull StateMachine<T> machine) {
		for (Entry<String, Graph<T>> e : machine.getGraphs().entrySet()) {
			
		}
	}
/*
	private Set<DisplayNode> getDisplayNodes(StateMachine machine) {
		Set<DisplayNode> displayNodes = new HashSet<DisplayNode>();
		
		
		
		for (String nodeId : nodes) {
			if (!machinesMap.containsKey(nodeId)) {
				DisplayNode node = new DisplayNode(nodeId, nodeId);

				NodeStyleBuilder acceptingBuilder = new NodeStyleBuilder();
				acceptingBuilder.setFillColor1("#40C0C0");
				acceptingBuilder.setShape(NodeShape.DIAMOND);

				NodeStyleBuilder startBuilder = new NodeStyleBuilder();
				startBuilder.setFillColor1("#F04040");
				startBuilder.setShape(NodeShape.ELLIPSE);

				if (acceptingStates.contains(nodeId)) {
					node.withNodeStyle(acceptingBuilder);
				} else if (startStateId.equals(nodeId)) {
					node.withNodeStyle(startBuilder);
				}
				displayNodes.add(node);
			}
		}
		return displayNodes;
	}

	private Set<DisplayGroup> getDisplayGroups() {
		Set<DisplayGroup> groups = new HashSet<DisplayGroup>();
		for (Map.Entry<String, StandardStateMachine> entry : machinesMap.entrySet()) {
			DisplayGroup group = entry.getValue().getGroup();
			groups.add(group);
		}
		return groups;
	}

	private Set<DisplayEdge> getDisplayEdges() {
		int arcId = 1;
		Set<DisplayEdge> displayEdges = new HashSet<DisplayEdge>();
		for (String nodeId : graph.getKeys()) {
			Map<Sequence, Set<String>> maps = graph.get(nodeId);

			for (Map.Entry<Sequence, Set<String>> entry : maps.entrySet()) {
				Sequence key = entry.getKey();
				Set<String> value = entry.getValue();

				for (String targetId : value) {
					DisplayEdge edge = new DisplayEdge(machineId + ":arc-" + arcId, key.toString(), nodeId, targetId);
					displayEdges.add(edge);
					arcId++;
				}
			}
		}
		return displayEdges;
	}
	*/
}
