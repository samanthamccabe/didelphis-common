package org.didelphis.common.utilities.graphs.node;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 1/11/2015
 */
public enum NodeShape {
	RECTANGLE("rectangle"),
	ROUND_RECTANGLE("roundrectangle"),
	ELLIPSE("ellipse"),
	PARALLELOGRAM("parallelogram"),
	HEXAGON("hexagon"),
	TRIANGLE("triangle"),
	OCTAGON("octagon"),
	DIAMOND("diamond");
	// TRAPEZOID
	// TRAPEZOID2

	private final String value;

	private NodeShape(String v) {
		value = v;
	}

	public String toString() {
		return value;
	}
}
