package org.didelphis.common.structures;

/**
 * This interface defines a general contract for a set of methods used in many
 * classes. It can be used to indicate some behavior found in
 * {@link java.util.Collection} but is more general.
 * 
 * Specifically, these are objects which can be said to have a size, and be
 * emptied of contents.
 * 
 * Implementing classes are very likely to be {@link Iterable} but
 * 
 * @author samantha.mccabe@didelphis.org
 */
public interface Structure {
	
	/**
	 * Returns the size of the maps, based on the totaly number of values or
	 * unique key pairs
	 * @return the number of values in the maps; guaranteed to be greater than 0
	 */
	int size();

	/**
	 * Tests if the maps is empty.
	 * @return true iff there is at least one key pair.
	 */
	boolean isEmpty();

	/**
	 * Deletes all contents from the maps.
	 * @return true iff contents were deleted; if the maps was already empty,
	 * this operation will return false.
	 */
	boolean clear();
}
