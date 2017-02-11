package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.tuples.Triple;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Collection;

/**
 * Created by samantha on 1/15/17.
 */
public interface TwoKeyMap<T, U, V> extends Iterable<Triple<T, U, V>> {

	V get(T k1, U k2);

	void put(T k1, U k2, V value);

	boolean contains(T k1, U k2);

	Collection<Tuple<T,U>> keys();

}
