/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.structures.contracts;

import lombok.NonNull;

import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Tuple;
import org.didelphis.utilities.Safe;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Interface {@code SymmetricallyAccessible}
 *
 * Designates a data structure with two keys or indices as obeying the contract
 * that the output of a method {@code M(k1, k2, ...)} is always equal to
 * {@code M(k2, k1, ...)} where only the order of {@code k1} and {@code k2}
 * differ.
 *
 * This is used both in symmetrical two key maps where the order of the keys is
 * irrelevant or in symmetrical matrices where the contents of {@code [i,j]} are
 * guaranteed to be equal to {@code [j, i]}.
 *
 * @param <K> the type of keys used by the object
 *
 * @since 0.2.0
 */
public interface SymmetricallyAccessible<K> {

	/**
	 * Retrieves the canonical ordering for the provided key pair. An ordering
	 * is canonical when only one ordering is stored in an underlying data
	 * structure.
	 * See {@link  org.didelphis.structures.maps.SymmetricalTwoKeyMap}
	 *
	 * @param k1 a key
	 * @param k2 another key
	 *
	 * @return the canonical ordering of the key pair used in the underlying
	 *      structure.
	 */
	@NonNull
	default Tuple<K, K> canonicalKeyPair(@Nullable K k1, @Nullable K k2) {
		if (k1 instanceof Comparable && k2 instanceof Comparable) {
			//noinspection unchecked
			Comparable<K> c1 = (Comparable<K>) k1;
			int compare = c1.compareTo(k2);
			return compare < 0 ? new Couple<>(k1, k2) : new Couple<>(k2, k1);
		} else if (Objects.equals(k1, k2)) {
			return new Couple<>(k1, k2);
		} else {
			int compare = Integer.compare(Safe.hashCode(k1), Safe.hashCode(k2));
			return compare < 0 ? new Couple<>(k1, k2) : new Couple<>(k2, k1);
		}
	}

	/**
	 * Retrieves the canonical ordering for the provided key pair. An ordering
	 * is <i>canonical</i> when only one ordering is stored in an underlying
	 * data structure.
	 *
	 * @param tuple a {@link Tuple} containing the keys whose canonical
	 *      representation will be determined
	 *
	 * @return the canonical ordering of the key pair used in the underlying
	 *      structure.
	 */
	@NonNull
	default Tuple<K, K> canonicalKeyPair(
			@NonNull Tuple<? extends K, ? extends K> tuple
	) {
		return canonicalKeyPair(tuple.getLeft(), tuple.getRight());
	}
}
