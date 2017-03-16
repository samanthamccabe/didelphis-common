package org.didelphis.common.utilities.graphs.edge;

import org.didelphis.common.utilities.graphs.DisplayElement;

/**
 * Author: goats
 * Created: 1/1/2015
 */
public class DisplayEdge implements DisplayElement {

	private final String edgeId;
	private final String edgeLabel;

	private final String sourceId;
	private final String targetId;

	private final EdgeLabelBuilder labelBuilder = new EdgeLabelBuilder();
	private final EdgeStyleBuilder styleBuilder = new EdgeStyleBuilder();

	// Color, Style  <y:LineStyle color="#000000" type="line" width="1.0"/>

	public DisplayEdge(String idParam, String label, String source, String target) {
		edgeId = idParam;
		edgeLabel = label;
		sourceId = source;
		targetId = target;
	}

	@Override
	public String getId() {
		return edgeId;
	}

	@Override
	public String toGraphML() {
		return toGraphML(0);
	}

	@Override
	public String toGraphML(int indentCount) {
		StringBuilder sb = new StringBuilder();

		sb.append("<edge id=\"")
				.append(edgeId)
				.append("\" source=\"")
				.append(sourceId)
				.append("\" target=\"")
				.append(targetId)
				.append("\">\n");

		sb.append("  <data key=\"d10\">\n");

		sb.append("    <y:PolyLineEdge>\n");

		// Interesting data goes here
		sb.append(styleBuilder.getGraphML(6));
		sb.append(labelBuilder.getGraphML(6, edgeLabel));

		// Path style: do we need this?
		sb.append("      <y:Path sx=\"0.0\" sy=\"0.0\" tx=\"0.0\" ty=\"0.0\"/>\n");
		sb.append("      <y:BendStyle smoothed=\"false\"/>\n");

		// Interesting data ends here
		sb.append("    </y:PolyLineEdge>\n");
		sb.append("  </data>\n");
		sb.append("</edge>\n");

		return sb.toString();
	}

}
