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

package org.didelphis.structures.graph;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.didelphis.structures.maps.interfaces.TwoKeyMultiMap;
import org.didelphis.structures.tuples.Triple;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Utility Class {@code GraphUtils}
 *
 * Provides functionality for generating Graph Modeling Language (GML) files
 * from a number of data types. This is especially useful for debugging classes
 * in the package {@link org.didelphis.language.automata}, and for developing
 * implementations based on these classes.
 * 
 * @date 1/26/18
 */
@UtilityClass
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
public class GraphUtils {

	@NonNull
	public static <T> String graphToGML(@NonNull
			TwoKeyMultiMap<String, T, String> graph, boolean useRealId) {
		Set<String> nodes = new HashSet<>();
		Set<Triple<String, T, String>> edges = new HashSet<>();
		for (Triple<String, T, Collection<String>> triple : graph) {
			String source = triple.getFirstElement();
			nodes.add(source);
			T arc = triple.getSecondElement();
			for (String target : triple.getThirdElement()) {
				nodes.add(target);
				edges.add(new Triple<>(source, arc, target));
			}
		}
		return buildGML(nodes, edges, useRealId);
	}

	private static String buildGML(
			@NonNull Set<Node> nodes,
			@NonNull Set<Edge> edges
	) {
		StringBuilder sb = new StringBuilder();

		sb.append("graph [\n");
		sb.append("\thierarchic\t1\n");
		sb.append("\tlabel\t\"\"\n");
		sb.append("\tdirected\t1\n");

		for (Node node : nodes) {
			sb.append(node);
		}
		
		for (Edge edge : edges) {
			sb.append(edge);
		}
		
		// Close and return
		return sb.append(']').toString();
	}
	
	@NonNull
	private static <T> String buildGML(
			@NonNull Set<String> nodes,
			@NonNull Set<Triple<String, T, String>> edges,
			boolean useRealId
	) {
		StringBuilder sb = new StringBuilder();

		sb.append("graph [\n");
		sb.append("\thierarchic\t1\n");
		sb.append("\tlabel\t\"\"\n");
		sb.append("\tdirected\t1\n");
		
		int index = 0;
		Map<String, Integer> indices = new HashMap<>();
		for (String node : nodes) {
			sb.append("\tnode [\n");
			sb.append("\t\tid\t").append(index).append('\n');
			Object id = useRealId ? node : index;
			sb.append("\t\tlabel\t\"").append(id).append("\"\n");
			sb.append("\t]\n");
			indices.put(node, index);
			index++;
		}

		for (Triple<String, T, String> edge : edges) {

			String source = edge.getFirstElement();
			String target = edge.getThirdElement();

			T arc = edge.getSecondElement();

			String rawString = Objects.toString(arc);
			
			if (rawString.equals("\t")) {
				rawString = "\\t";
			} else if (rawString.equals("\n")) {
				rawString = "\\n";
			} else if (rawString.equals("\r")) {
				rawString = "\\r";
			} else if (rawString.equals(" ")) {
				rawString = "\u2017";
			} else if (rawString.equals("\f")) {
				rawString = "\\f";
			}
			
			String string = rawString.replaceAll("\\s+\\(\\w+\\)", "");

			sb.append("\tedge [\n");
			sb.append("\t\tsource\t").append(indices.get(source)).append('\n');
			sb.append("\t\ttarget\t").append(indices.get(target)).append('\n');
			sb.append("\t\tlabel\t\"").append(string).append("\"\n");
			sb.append("\t]\n");
		}

		sb.append(']');

		return sb.toString();
	}

	@Data
	@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
	@RequiredArgsConstructor
	private static final class Node {
		int id;
		String label;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\tnode [\n");
			sb.append("\t\tid\t").append(id).append('\n');
			sb.append("\t\tlabel\t\"").append(label).append("\"\n");
			sb.append("\t]\n");
			return sb.toString();
		}
	}
	
	@Data
	@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
	@RequiredArgsConstructor
	private static final class Edge {
		
		int source;
		int target;
		String arc;
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\tedge [\n");
			sb.append("\t\tsource\t").append(source).append('\n');
			sb.append("\t\ttarget\t").append(target).append('\n');
			sb.append("\t\tlabel\t\"").append(arc).append("\"\n");
			sb.append("\t]\n");
			return sb.toString();
		}
	}
}
