package org.didelphis.structures.graph;

/**
 * Interface {@code Arc}
 *
 */
@FunctionalInterface
public interface Arc<S> {
	
	int match(S sequence, int index);
	
}
