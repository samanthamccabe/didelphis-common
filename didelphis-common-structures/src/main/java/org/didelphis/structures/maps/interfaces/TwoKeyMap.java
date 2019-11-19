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

package org.didelphis.structures.maps.interfaces;

import lombok.NonNull;

import org.didelphis.structures.Structure;
import org.didelphis.structures.contracts.Streamable;
import org.didelphis.structures.tuples.Triple;
import org.didelphis.structures.tuples.Tuple;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Interface {@code TwoKeyMap}
 *
 * @since 0.1.0
 */
public interface TwoKeyMap<T, U, V> extends Streamable<Triple<T, U, V>>,
		Structure {

	/**
	 * Return the value stored under the two keys
	 *
	 * @param k1 the first key; may be {@code null}
	 * @param k2 the second key; may be {@code null}
	 *
	 * @return the value stored under the given keys; may be {@code null} if
	 *      either the keys have no associated value or if a {@code null} has
	 *      been stored explicitly
	 */
	@Nullable V get(@Nullable T k1, @Nullable U k2);

	/**
	 * Inserts a new value under the two keys
	 *
	 * @param k1 the first key; may be {@code null}
	 * @param k2 the second key; may be {@code null}
	 * @param value the value to be inserted under the given keys
	 */
	void put(@Nullable T k1, @Nullable U k2, @Nullable V value);

	/**
	 * Checks whether a value is present under the two keys
	 *
	 * @param k1 the first key; may be {@code null}
	 * @param k2 the second key; may be {@code null}
	 *
	 * @return true if the maps contains a value under the two keys
	 */
	boolean contains(@Nullable T k1, @Nullable U k2);

	/**
	 * Checks whether a key is present as part of a key-pair in the first
	 * position
	 *
	 * @param k1 the first key; may be {@code null}
	 *
	 * @return true if a key is present as part of a key-pair
	 */
	boolean containsFirstKey(@Nullable T k1);

	/**
	 * Checks whether a key is present as part of a key-pair in the second
	 * position
	 *
	 * @param k2 the first key; may be {@code null}
	 *
	 * @return true if a key is present as part of a key-pair
	 */
	boolean containsSecondKey(@Nullable U k2);

	/**
	 * Returns {@code true} if any keys map to the provided value.
	 *
	 * @param value value whose presence in this map is to be tested
	 *
	 * @return {@code true} if this map maps one or more keys to the
	 *      specified value
	 */
	boolean containsValue(@Nullable V value);

	/**
	 * Updates the stored value using the one provided; by default this is the
	 * same as calling {@link TwoKeyMap#put(T, U, V)} but implementations may
	 * override this to support behavior like adding numbers, appending strings,
	 * or calling other methods on {@param value} according to its type
	 *
	 * @param k1 the first key; may be {@code null}
	 * @param k2 the second key; may be {@code null}
	 * @param value the value with which to update the current value
	 */
	default void update(@Nullable T k1, @Nullable U k2, @Nullable V value) {
		put(k1, k2, value);
	}

	/**
	 * @return a collection of tuples containing the maps's key pairs;
	 *      guaranteed to not be {@code null}
	 */
	@NonNull Collection<Tuple<T, U>> keys();

	/**
	 * Returns the number of key pairs in this map.  If the map contains more
	 * than {@code Integer.MAX_VALUE} key pairs, this returns {@code
	 * Integer.MAX_VALUE}.
	 *
	 * @return the number of key pairs in this map
	 */
	int size();

	/**
	 * Remove and return the value stored under the two keys
	 *
	 * @param k1 the first key; may be {@code null}
	 * @param k2 the second key; may be {@code null}
	 *
	 * @return the value stored under the given keys; may be {@code null} if
	 *      either the keys have no associated value or if a {@code null} has
	 *      been stored explicitly
	 */
	@Nullable
	V removeKeys(@Nullable T k1, @Nullable U k2);

	/**
	 * A null-safe version of {@link #contains(T, U)}
	 *
	 * @param k1 the first key; may be {@code null}
	 * @param k2 the second key; may be {@code null}
	 *
	 * @return true iff the map contains a non-{@code null} value under the two
	 *      keys
	 */
	default boolean containsNotNull(@Nullable T k1, @Nullable U k2) {
		return contains(k1, k2) && get(k1, k2) != null;
	}

	/**
	 * Analogous to {@link Map#getOrDefault(Object, Object)} but for two keys.
	 * It performs a {@link TwoKeyMap#contains(T, U)} check and either returns
	 * the stored value, or the provided default.
	 *
	 * @param k1 the first key; may be null
	 * @param k2 the second key; may be null
	 * @param value the default value to be returned in case to stored value is
	 *      found or is null
	 *
	 * @return the retrieved value if present and not null, or the provided
	 *      default if not.
	 */
	@NonNull
	default V getOrDefault(@Nullable T k1, @Nullable U k2, @NonNull V value) {
		if (contains(k1, k2)) {
			V v = get(k1, k2);
			return v == null ? value : v;
		}
		return value;
	}

	/**
	 * Copies all of the mappings from the specified map to this map
	 * (optional operation).  The effect of this call is equivalent to that
	 * of calling {@link #put(T, U, V)} on this map once for each mapping from
	 * each key pair to value in the map.
	 *
	 * @param map mappings to be stored in this map; not {@code null}
	 *
	 * @throws UnsupportedOperationException if the {@code putAll} operation
	 * 		is not supported by this map
	 * @throws ClassCastException if the class of a key or value in the
	 * 		specified map prevents it from being stored in this map
	 * @throws IllegalArgumentException if some property of a key or value in
	 * 		the specified map prevents it from being stored in this map
	 */
	void putAll(@NonNull TwoKeyMap<T, U, V> map);
}
