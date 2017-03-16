package org.didelphis.common.utilities.graphs.node;

/**
 * Created by samantha on 3/29/15.
 */

public class NodeLabelBuilder {

	// Label ---------
	//     Style: ENUM
	// Placement: ENUM
	private String textColor = "#000000";
	private String font      = "Dialog";
	private int    fontSize  = 10;

	public String getGraphML(int indentCount, String labelText) {

		StringBuilder indent = new StringBuilder(indentCount);
		for (int i = 0; i < indentCount; i++) {
			indent.append(" ");
		}

		StringBuilder sb = new StringBuilder();
		/* Groups are a little different
		 * <y:NodeLabel alignment="right" autoSizePolicy="node_width" backgroundColor="#EBEBEB" borderDistance="0.0" fontFamily="Dialog" fontSize="15" fontStyle="plain" hasLineColor="false" height="30.0" modelName="internal" modelPosition="t" textColor="#000000" visible="true" width="60.0" x="0.0" y="0.0">Folder 1</y:NodeLabel>");
		 */
		sb.append("<y:NodeLabel alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"")
			.append(font)
			.append("\" fontSize=\"")
			.append(fontSize)
			.append("\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" ")
			.append("height=\"18.0\" modelName=\"custom\" textColor=\"")

			.append(textColor)
			.append("\" visible=\"true\" width=\"10.0\" x=\"9.0\" y=\"5.0\">")
			.append(labelText)
			.append("<y:LabelModel>\n");

		sb.append(indent).append("    <y:SmartNodeLabelModel distance=\"4.0\"/>\n");
		sb.append(indent).append("  </y:LabelModel>\n");
		sb.append(indent).append("  <y:ModelParameter>\n");
		sb.append(indent).append("    <y:SmartNodeLabelModelParameter labelRatioX=\"0.0\" labelRatioY=\"0.0\" nodeRatioX=\"0.0\" nodeRatioY=\"0.0\" offsetX=\"0.0\" offsetY=\"0.0\" upX=\"0.0\" upY=\"-1.0\"/>\n");
		sb.append(indent).append("  </y:ModelParameter>\n");
		sb.append(indent).append("</y:NodeLabel>\n");

		return sb.toString();
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
