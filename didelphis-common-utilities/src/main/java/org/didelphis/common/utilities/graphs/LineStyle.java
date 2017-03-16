package org.didelphis.common.utilities.graphs;

/**
 * Author: goats
 * Created: 1/1/2015
 */
public enum LineStyle {

	LINE  ("line"),
	DOTTED("dotted"),
	DASHED("dashed");

	private final String value;

	private LineStyle(String v) {
		value = v;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}
}
