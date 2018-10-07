package org.didelphis.language.automata;

/**
 * Interface {@code GraphEdge}
 *
 * @author Samantha Fiona McCabe
 */
public interface GraphEdge<T> {
	int matches(T input);
}
