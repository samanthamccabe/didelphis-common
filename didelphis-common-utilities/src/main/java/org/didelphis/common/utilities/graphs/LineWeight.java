package org.didelphis.common.utilities.graphs;

/**
 * Author: goats
 * Created: 1/1/2015
 */
public enum LineWeight {
	ONE  ("1.0"),
	TWO  ("2.0"),
	THREE("3.0"),
	FOUR ("4.0"),
	FIVE ("5.0");

	private final String value;
	private LineWeight(String v) { value = v; }

	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}
}
