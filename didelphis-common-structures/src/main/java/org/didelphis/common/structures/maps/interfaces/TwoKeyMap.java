package org.didelphis.common.structures.maps.interfaces;

import org.didelphis.common.structures.tuples.Triple;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Collection;
import java.util.Map;

/**
 * Created by samantha on 1/15/17.
 */
public interface TwoKeyMap<T, U, V>
		extends Map<T, Map<U, V>>, Iterable<Triple<T, U, V>> {

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

}
