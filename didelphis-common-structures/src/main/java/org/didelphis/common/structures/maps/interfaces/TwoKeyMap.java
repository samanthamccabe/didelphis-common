package org.didelphis.common.structures.maps.interfaces;

import org.didelphis.common.structures.tuples.Triple;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Collection;

/**
 * Created by samantha on 1/15/17.
 */
public interface TwoKeyMap<T, U, V>
		extends Iterable<Triple<T, U, V>> {

	/**
	 * Return the value stored under the two keys
	 *
	 * @param k1 the first key; may be null
	 * @param k2 the second key; may be null
	 *
	 * @return the value stored under the given keys
	 */
	V get(T k1, U k2);

	/**
	 * Inserts a new value under the two keys
	 *
	 * @param k1 the first key; may be null
	 * @param k2 the second key; may be null
	 * @param value the value to be inserted under the given keys
	 */
	void put(T k1, U k2, V value);

	/**
	 * Checks whether a value is present under the two keys
	 *
	 * @param k1 the first key; may be null
	 * @param k2 the second key; may be null
	 *
	 * @return true if the map contains a value under the two keys
	 */
	boolean contains(T k1, U k2);

	/**
	 * @return a collection of tuples containing the map's key pairs
	 */
	Collection<Tuple<T, U>> keys();

	/**
	 * Removes the value associated with the provided keys.
	 * @param k1
	 * @param k2
	 * @return the value associated with the provided key pair, if it exists; if
	 * no key pair exists, this operation will return null;
	 */
	V remove(T k1, U k2);

	/**
	 * Returns the size of the map, based on the totaly number of values or
	 * unique key pairs
	 * @return the number of values in the map; guaranteed to be greater than 0
	 */
	int size();

	/**
	 * Tests if the map is empty.
	 * @return true iff there is at least one key pair.
	 */
	boolean isEmpty();

	/**
	 * Deletes all contents from the map.
	 * @return true iff contents were deleted; if the map was already empty,
	 * this operation will return false.
	 */
	boolean clear();
}
