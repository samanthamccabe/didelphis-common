package org.didelphis.common.utilities.graphs.node;

import org.didelphis.common.utilities.graphs.LineStyle;
import org.didelphis.common.utilities.graphs.LineWeight;

/**
 * Created by samantha on 3/29/15.
 */
public class NodeStyleBuilder {

	private String fillColor1 = "#C0C0C0";
	private String fillColor2 = "";

	private String     lineColor   = "#000000";
	private LineStyle borderStyle = LineStyle.LINE;
	private LineWeight lineWeight  = LineWeight.ONE;

	private NodeShape shape = NodeShape.RECTANGLE;

	public void setFillColor1(String fillColor1) {
		this.fillColor1 = fillColor1;
	}

	public void setFillColor2(String fillColor2) {
		this.fillColor2 = fillColor2;
	}

	public void setLineColor(String lineColor) {
		this.lineColor = lineColor;
	}

	public void setBorderStyle(LineStyle borderStyle) {
		this.borderStyle = borderStyle;
	}

	public void setLineWeight(LineWeight lineWeight) {
		this.lineWeight = lineWeight;
	}

	public void setShape(NodeShape shape) {
		this.shape = shape;
	}

	public String getGraphML(int indentCount) {
		StringBuilder sb = new StringBuilder();

		StringBuilder indent = new StringBuilder(indentCount);
		for (int i = 0; i < indentCount; i++) {
			indent.append(" ");
		}

		sb.append(indent);
		sb.append("<y:Fill color=\"")
			.append(fillColor1)
			.append("\" transparent=\"false\"/>\n");

		sb.append(indent);
		sb.append("<y:BorderStyle color=\"")
			.append(lineColor)
			.append("\" type=\"")
			.append(borderStyle)
			.append("\" width=\"")
			.append(lineWeight)
			.append("\"/>\n");

		sb.append(indent);
		sb.append("<y:Shape type=\"")
			.append(shape)
			.append("\"/>\n");

		return sb.toString();
	}
}
