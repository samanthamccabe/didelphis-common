package org.didelphis.common.structures.contracts;

import org.didelphis.common.structures.tuples.Tuple;

import java.util.Objects;

/**
 * Designates a data structure with two keys or indices as obeying the contract 
 * that the output of a method {@code M(k1, k2, ...)} is always equal to 
 * {@code M(k2, k1, ...)} wher only the order of {@code k1} and {@code k2}
 * differ.
 * 
 * This is used both in symmetrical two key maps where the order of the keys is
 * irrelevant or in symmetrical matrices where the contents of {@code [i,j]} are
 * guaranteed to be equal to {@code [j, i]}.
 * 
 * @param <K> the type of keys used by the object
 */
public interface SymmetricallyAccessible<K> {

	/**
	 * Retrieves the canonical ordering for the provided key pair. An ordering
	 * is canonical when only one ordering is stored in an underlying data
	 * structure.
	 * See {@link org.didelphis.common.structures.maps.SymmetricalTwoKeyMap}
	 *
	 * @param k1 a key
	 * @param k2 another key
	 *
	 * @return the canonical ordering of the key pair used in the underlying
	 * structure. Null iff no ordering of {@code k1} and {@code k2} has an
	 * associated value.
	 */
	default Tuple<K, K> canonicalKeyPair(K k1, K k2) {
		if (k1 instanceof Comparable && k2 instanceof Comparable) {
			@SuppressWarnings("unchecked") 
			Comparable<Object> c1 = (Comparable<Object>) k1;
			int compare = c1.compareTo(k2);
			return compare < 0 ? new Tuple<>(k1, k2) : new Tuple<>(k2, k1);
		} else if (Objects.equals(k1, k2)) {
			return new Tuple<>(k1, k2);
		} else {
			int compare = Integer.compare(k1.hashCode(), k2.hashCode());
			return compare < 0 ? new Tuple<>(k1, k2) : new Tuple<>(k2, k1);
		}
	}
}
