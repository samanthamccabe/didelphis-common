/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.structures.contracts;

import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Tuple;
import lombok.NonNull;
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
 * @author Samantha Fiona McCabe
 * @date 2017-05-03
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
	 * 		structure.
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
			int compare = Integer.compare(k1.hashCode(), k2.hashCode());
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
	 * 		structure.
	 */
	@NonNull
	default Tuple<K, K> canonicalKeyPair(@NonNull Tuple<K, K> tuple) {
		return canonicalKeyPair(tuple.getLeft(), tuple.getRight());
	}
}
