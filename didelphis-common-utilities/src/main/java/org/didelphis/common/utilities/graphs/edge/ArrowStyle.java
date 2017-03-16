package org.didelphis.common.utilities.graphs.edge;

/**
 * Author: goats
 * Created: 1/1/2015
 */
public enum ArrowStyle {
	DELTA("delta"),
	WHITE_DELTA("white_delta"),
	NONE("none"),
	PLAIN("plain"),
	SHORT("short"),
	CONVEX("convex"),
	CONCAVE("concave"),
	CIRCLE("circle"),
	DIAMOND("diamond"),
	WHITE_DIAMOND("white_diamond"),
	TRANSPARENT_CIRCLE("transparent_circle"),
	DASH("dash"),
	SKEWED_DASH("skewed_dash"),
	T_SHAPE("t_shape"),
	CROWS_FOOT_ONE("crows_foot_one"),
	CROWS_FOOT_MANY("crows_foot_many"),
	CROWS_FOOT_OPTIONAL("crows_foot_optional"),
	CROWS_FOOT_ONE_OPTIONAL("crows_foot_one_optional"),
	CROWS_FOOT_MANY_OPTIONAL("crows_foot_many_optional"),
	CROWS_FOOT_ONE_MANDATORY("crows_foot_one_mandatory"),
	CROWS_FOOT_MANY_MANDATORY("crows_foot_many_mandatory");

	private final String value;
	ArrowStyle(String v) {
		value = v;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}
}
