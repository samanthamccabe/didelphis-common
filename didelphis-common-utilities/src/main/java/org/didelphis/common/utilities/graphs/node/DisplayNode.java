package org.didelphis.common.utilities.graphs.node;


import org.didelphis.common.utilities.graphs.DisplayElement;

/**
 * Author: goats
 * Created: 1/1/2015
 */
public class DisplayNode implements DisplayElement {

	private final String nodeId;
	private final String labelText;

	// Geometry
	private double width     = 40.0;
	private double height    = 40.0;
	private double xPosition =  0.0;
	private double yPosition =  0.0;

	private NodeStyleBuilder styleBuilder = new NodeStyleBuilder();
	private NodeLabelBuilder labelBuilder = new NodeLabelBuilder();

	public DisplayNode(String idParam, String labelParam) {
		nodeId = idParam;
		labelText = labelParam;
	}

	@Override
	public String getId() {
		return nodeId;
	}

	@Override
	public String toGraphML() {
		return toGraphML(0);
	}

	@Override
	public String toGraphML(int indentCount) {
		StringBuilder sb = new StringBuilder();

		String indent = "";
		for (int i = 0; i < indentCount; i++) {
			indent += " ";
		}

		sb.append(indent);
		sb.append("<node id=\"")
			.append(nodeId)
			.append("\">\n");

		sb.append(indent).append("  <data key=\"d6\">\n");
		sb.append(indent).append("    <y:ShapeNode>\n");

		// Interesting data goes here
		sb.append(indent);
		sb.append("      <y:Geometry height=\"")
			.append(height)
			.append("\" width=\"")
			.append(width)
			.append("\"/>\n");

		sb.append(indent).append(styleBuilder.getGraphML(indentCount));
		sb.append(indent).append(labelBuilder.getGraphML(indentCount, labelText));

		// Interesting data ends here
		sb.append(indent).append("    	</y:ShapeNode>\n");
		sb.append(indent).append("  </data>\n");
		sb.append(indent).append("</node>\n");

		return sb.toString();
	}

	public void withNodeStyle(NodeStyleBuilder builder) {
		styleBuilder = builder;
	}

	public void withLabelStyle(NodeLabelBuilder builder) {
		labelBuilder = builder;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setxPosition(double xPosition) {
		this.xPosition = xPosition;
	}

	public void setyPosition(double yPosition) {
		this.yPosition = yPosition;
	}
}
