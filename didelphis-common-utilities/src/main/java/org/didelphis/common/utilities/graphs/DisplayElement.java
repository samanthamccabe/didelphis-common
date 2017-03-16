package org.didelphis.common.utilities.graphs;

/**
 * Author: goats
 * Created: 1/1/2015
 */
public interface DisplayElement {

	String getId();

	String toGraphML();

	String toGraphML(int indentCount);
}
