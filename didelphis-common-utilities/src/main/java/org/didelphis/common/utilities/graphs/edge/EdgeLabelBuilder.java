package org.didelphis.common.utilities.graphs.edge;

/**
 * Created by samantha on 3/29/15.
 */
public class EdgeLabelBuilder {

	String labelBackgroundColor = "";
	String labelLineColor       = "";

	String fontStyle = "plain";
	String textColor = "#000000";
	String fontType  = "Dialog";

	int fontSize = 10;

	double width    = 26.0;
	double height   = 14.0;
	double distance = 2.0;

	public void setLabelBackgroundColor(String labelBackgroundColor) {
		this.labelBackgroundColor = labelBackgroundColor;
	}

	public void setLabelLineColor(String labelLineColor) {
		this.labelLineColor = labelLineColor;
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public void setFontType(String fontType) {
		this.fontType = fontType;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getGraphML(int indentCount, String edgeLabel) {
		StringBuilder sb = new StringBuilder();

		StringBuilder indent = new StringBuilder(indentCount);
		for (int i = 0; i < indentCount; i++) {
			indent.append(" ");
		}

		sb.append(indent);
		sb.append("<y:EdgeLabel alignment=\"center\" anchorX=\"52\" anchorY=\"24\" ");

		if (labelBackgroundColor != null && !labelBackgroundColor.isEmpty()) {
			sb.append(" backgroundColor=\"")
					.append(labelBackgroundColor)
					.append("\" ");
		}

		if (labelLineColor != null && !labelLineColor.isEmpty()) {
			sb.append(" lineColor=\"")
					.append(labelLineColor)
					.append("\" ");
		}

		sb.append(" configuration=\"AutoFlippingLabel\" distance=\"")
			.append(distance)
			.append("\" fontFamily=\"")
			.append(fontType)
			.append("\" fontSize=\"")
			.append(fontSize)
			.append("\" fontStyle=\"")
			.append(fontStyle)
			.append("\" height=\"")
			.append(height)
			.append("\"")
			.append(" modelName=\"custom\" preferredPlacement=\"center_right\" ratio=\"0.5\" textColor=\"")
			.append(textColor)
			.append("\" upX=\"0.0\" upY=\"-1.0\" visible=\"true\" width=\"")
			.append(width)
			.append("\" x=\"52.0\" y=\"6.0\">")
			.append(edgeLabel)
			.append("<y:LabelModel>\n");

//		sb.append("<y:RotatedDiscreteEdgeLabelModel angle=\"0.0\" autoRotationEnabled=\"true\" candidateMask=\"448\" distance=\"2.0\" positionRelativeToSegment=\"false\"/>");
		sb.append("</y:LabelModel>");
		sb.append("<y:ModelParameter>");
		sb.append("<y:RotatedDiscreteEdgeLabelModelParameter position=\"center\"/>");
		sb.append("</y:ModelParameter>");
		sb.append("<y:PreferredPlacementDescriptor angle=\"0.0\" angleOffsetOnRightSide=\"0\" angleReference=\"absolute\" angleRotationOnRightSide=\"co\" distance=\"-1.0\" placement=\"center\" side=\"right\" sideReference=\"relative_to_edge_flow\"/>");
		sb.append("</y:EdgeLabel>");

		return sb.toString();
	}
}
