package org.didelphis.common.utilities.graphs.edge;

import org.didelphis.common.utilities.graphs.LineStyle;
import org.didelphis.common.utilities.graphs.LineWeight;

/**
 * Created by samantha on 3/29/15.
 */
public class EdgeStyleBuilder {

	private ArrowStyle sourceStyle = ArrowStyle.NONE;
	private ArrowStyle targetStyle = ArrowStyle.DELTA;

	private LineWeight arrowWidth = LineWeight.ONE;
	private LineStyle  lineStyle  = LineStyle.LINE;
	private String     arrowColor = "#000000";

	public EdgeStyleBuilder() {}

	public String getGraphML(int indentCount) {
		StringBuilder sb = new StringBuilder();

		String indent = "";
		for (int i = 0; i < indentCount; i++) {
			indent += " ";
		}

		sb.append(indent);
		sb.append("<y:LineStyle color=\"")
				.append(arrowColor)
				.append("\" type=\"")
				.append(lineStyle)
				.append("\" width=\"")
				.append(arrowWidth)
				.append("\"/>")
				.append("\n");

		sb.append(indent);
		sb.append("<y:Arrows source=\"")
				.append(sourceStyle)
				.append("\" target=\"")
				.append(targetStyle)
				.append("\"/>")
				.append("\n");

		return sb.toString();
	}
}
