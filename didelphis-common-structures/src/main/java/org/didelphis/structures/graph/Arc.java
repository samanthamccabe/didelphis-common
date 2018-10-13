package org.didelphis.structures.graph;

/**
 * Interface {@code Arc}
 *
 * @author Samantha Fiona McCabe
 */
@FunctionalInterface
public interface Arc<S> {
	
	int match(S sequence, int index);
	
}
