package org.didelphis.common.utilities.graphs.node;

import org.didelphis.common.utilities.graphs.DisplayElement;
import org.didelphis.common.utilities.graphs.GraphBuilder;
import org.didelphis.common.utilities.graphs.edge.DisplayEdge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 1/11/2015
 */
public class DisplayGroup implements DisplayElement {

	private final String nodeId;
	private final String labelText;

	private final Set<DisplayElement> graph;

	private NodeStyleBuilder styleBuilder = new NodeStyleBuilder();
	private NodeLabelBuilder labelBuilder = new NodeLabelBuilder();

	public static void main(String[] args) throws IOException {
		DisplayGroup group = new DisplayGroup("G-1", "Test Group");
		group.add(new DisplayNode("N-0", "N-0"));
		group.add(new DisplayNode("N-1", "N-1"));
		group.add(new DisplayEdge("X","X","N-0", "N-1"));

		GraphBuilder builder = new GraphBuilder();
		builder.addGroup(group);

		String graphML = builder.generateGraphML();

		File file = new File("test.graphml");
		Writer writer = new FileWriter(file);
		writer.write(graphML);

		writer.close();
	}


	public DisplayGroup(String id, String label) {
		nodeId = id;
		labelText = label;
		graph = new HashSet<>();
	}

	public void add(DisplayElement element) {
		graph.add(element);
	}

	public void addAll(Collection<DisplayElement> edgesParam) {
		graph.addAll(edgesParam);
	}

	public void addAllEdges(Collection<DisplayEdge> edgesParam) {
		graph.addAll(edgesParam);
	}

	public void addAllNodes(Collection<DisplayNode> nodesParam) {
		graph.addAll(nodesParam);
	}

	public void addAllGroups(Collection<DisplayGroup> nodesParam) {
		graph.addAll(nodesParam);
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String toGraphML() {
		return toGraphML(0);
	}

	@Override
	public String toGraphML(int indentCount) {
		StringBuilder sb = new StringBuilder();

		sb.append("<node id=\"")
			.append(nodeId)
			.append("\" yfiles.foldertype=\"group\">\n");

		sb.append("  <data key=\"d4\"/>\n");
		sb.append("  <data key=\"d5\"/>\n");
		sb.append("  <data key=\"d6\">\n");
		sb.append("    <y:ProxyAutoBoundsNode>\n");
		sb.append("      <y:Realizers active=\"0\">\n");

		// OPEN
		sb.append("        <y:GroupNode>\n");
		sb.append("          <y:Geometry height=\"90.0\" width=\"62.0\" x=\"0.0\" y=\"0.0\"/>\n");
		sb.append(styleBuilder.getGraphML(10));
		sb.append(labelBuilder.getGraphML(10, labelText));
		sb.append("          <y:State closed=\"false\" closedHeight=\"50.0\" closedWidth=\"50.0\" innerGraphDisplayEnabled=\"false\"/>\n");
		sb.append("          <y:Insets bottom=\"15\" bottomF=\"15.0\" left=\"15\" leftF=\"15.0\" right=\"15\" rightF=\"15.0\" top=\"15\" topF=\"15.0\"/>\n");
		sb.append("          <y:BorderInsets bottom=\"0\" bottomF=\"0.0\" left=\"1\" leftF=\"1.0\" right=\"1\" rightF=\"1.0\" top=\"0\" topF=\"0.0\"/>\n");
		sb.append("        </y:GroupNode>\n");

		// CLOSED
		sb.append("        <y:GroupNode>\n");
		sb.append("          <y:Geometry height=\"90.0\" width=\"62.0\" x=\"0.0\" y=\"0.0\"/>\n");
		sb.append(styleBuilder.getGraphML(10));
		sb.append(labelBuilder.getGraphML(10, labelText));
		sb.append("          <y:State closed=\"true\" closedHeight=\"50.0\" closedWidth=\"50.0\" innerGraphDisplayEnabled=\"false\"/>\n");
		sb.append("          <y:Insets bottom=\"5\" bottomF=\"5.0\" left=\"5\" leftF=\"5.0\" right=\"5\" rightF=\"5.0\" top=\"5\" topF=\"5.0\"/>\n");
		sb.append("          <y:BorderInsets bottom=\"0\" bottomF=\"0.0\" left=\"0\" leftF=\"0.0\" right=\"0\" rightF=\"0.0\" top=\"0\" topF=\"0.0\"/>\n");
		sb.append("        </y:GroupNode>\n");

		sb.append("      </y:Realizers>\n");
		sb.append("    </y:ProxyAutoBoundsNode>\n");
		sb.append("  </data>\n");
		sb.append("  <graph edgedefault=\"directed\" id=\"")
			.append(nodeId)
			.append(":\">\n");

		// Insert the subordinate nodes
		for (DisplayElement element : graph) {
			sb.append(element.toGraphML());
		}

		sb.append("  </graph>\n</node>");
		sb.append("\n");

		return sb.toString();
	}
}
