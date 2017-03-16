package org.didelphis.common.language.machines;

import org.didelphis.common.language.machines.interfaces.StateMachine;
import org.didelphis.common.language.phonetic.sequences.Sequence;
import org.didelphis.common.utilities.graphs.edge.DisplayEdge;
import org.didelphis.common.utilities.graphs.node.DisplayGroup;
import org.didelphis.common.utilities.graphs.node.DisplayNode;
import org.didelphis.common.utilities.graphs.node.NodeShape;
import org.didelphis.common.utilities.graphs.node.NodeStyleBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by samantha on 3/14/17.
 */
public final class MachineUtils {
	
	private MachineUtils() {}

	private void generate(StateMachine machine) {
		
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
			Map<Sequence, Set<String>> map = graph.get(nodeId);

			for (Map.Entry<Sequence, Set<String>> entry : map.entrySet()) {
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
