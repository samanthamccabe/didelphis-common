package org.didelphis.common.utilities.graphs;

import org.didelphis.common.utilities.graphs.edge.DisplayEdge;
import org.didelphis.common.utilities.graphs.node.DisplayGroup;
import org.didelphis.common.utilities.graphs.node.DisplayNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: goats
 * Created: 1/1/2015
 */
public class GraphBuilder {

	private static final String XML_DATA        = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
	private static final String XML_NAMESPACE   = "xmlns=\"http://graphml.graphdrawing.org/xmlns\" ";
	private static final String XSI_NAMESPACE   = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
	private static final String Y_NAMESPACE     = "xmlns:y=\"http://www.yworks.com/xml/graphml\" ";
	private static final String YED_NAMESPACE   = "xmlns:yed=\"http://www.yworks.com/xml/yed/3\" ";
	private static final String SCHEMA_LOCATION = "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\"";

	private final Set<DisplayEdge>  edges;
	private final Set<DisplayNode>  nodes;
	private final Set<DisplayGroup> groups;

	public static void main(String[] args) {
		GraphBuilder graphBuilder = new GraphBuilder();

		for (int i = 1; i <= 7; i++) {
			String label = "M-"+i;

			DisplayNode displayNode = new DisplayNode(label, label);
			int color = i + 2;

			graphBuilder.addNode(displayNode);
		}

		int i = 1;
		for (DisplayElement displayElement1 : graphBuilder.getNodes()) {
			String id1 = displayElement1.getId();
			for (DisplayElement displayElement2 : graphBuilder.getNodes()) {
				String id2 = displayElement2.getId();

				String idParam = "A-" + i;
				DisplayEdge displayEdge = new DisplayEdge(idParam, idParam, id1, id2);
				graphBuilder.addEdge(displayEdge);
				i++;
			}
		}
		System.out.println(graphBuilder.generateGraphML());
	}

	public GraphBuilder() {
		edges  = new HashSet<DisplayEdge>();
		nodes  = new HashSet<DisplayNode>();
		groups = new HashSet<DisplayGroup>();
	}

	public void addAllEdges(Collection<DisplayEdge> edgesParam) {
		edges.addAll(edgesParam);
	}

	public void addAllNodes(Collection<DisplayNode> nodesParam) {
		nodes.addAll(nodesParam);
	}

	public void addAllGroups(Collection<DisplayGroup> nodesParam) {
		groups.addAll(nodesParam);
	}

	public void addGroup(DisplayGroup group) {
		groups.add(group);
	}

	public void addNode(DisplayNode node) {
		nodes.add(node);
	}

	public Set<DisplayNode> getNodes() {
		return nodes;
	}

	public Set<DisplayEdge> getEdges() {
		return edges;
	}

	public Set<DisplayGroup> getGroups() {
		return groups;
	}

	public void addEdge(DisplayEdge edge) {
		edges.add(edge);
	}

	public String generateGraphML() {
		StringBuilder sb = new StringBuilder(XML_DATA);

		sb.append("<graphml ");
		sb.append(XML_NAMESPACE).append('\n');
		sb.append(XSI_NAMESPACE).append('\n');
		sb.append(Y_NAMESPACE).append('\n');
		sb.append(YED_NAMESPACE).append('\n');
		sb.append(SCHEMA_LOCATION).append('\n');
		sb.append('>').append('\n');

		sb.append("  <key for=\"graphml\" id=\"d0\" yfiles.type=\"resources\"/>\n");
		sb.append("  <key for=\"port\" id=\"d1\" yfiles.type=\"portgraphics\"/>\n");
		sb.append("  <key for=\"port\" id=\"d2\" yfiles.type=\"portgeometry\"/>\n");
		sb.append("  <key for=\"port\" id=\"d3\" yfiles.type=\"portuserdata\"/>\n");
		sb.append("  <key attr.name=\"url\" attr.type=\"string\" for=\"node\" id=\"d4\"/>\n");
		sb.append("  <key attr.name=\"description\" attr.type=\"string\" for=\"node\" id=\"d5\"/>\n");
		sb.append("  <key for=\"node\" id=\"d6\" yfiles.type=\"nodegraphics\"/>\n");
		sb.append("  <key attr.name=\"Description\" attr.type=\"string\" for=\"graph\" id=\"d7\"/>\n");
		sb.append("  <key attr.name=\"url\" attr.type=\"string\" for=\"edge\" id=\"d8\"/>\n");
		sb.append("  <key attr.name=\"description\" attr.type=\"string\" for=\"edge\" id=\"d9\"/>\n");
		sb.append("  <key for=\"edge\" id=\"d10\" yfiles.type=\"edgegraphics\"/>\n");
		sb.append("  <graph edgedefault=\"directed\" id=\"G\">\n");

		for (DisplayNode node : nodes) {
			sb.append(node.toGraphML(2));
		}

		for (DisplayGroup node : groups) {
			sb.append(node.toGraphML(2));
		}

		for (DisplayEdge edge : edges) {
			sb.append(edge.toGraphML(2));
		}

		sb.append("  </graph>");
		sb.append("</graphml>");
		return sb.toString();
	}
}
