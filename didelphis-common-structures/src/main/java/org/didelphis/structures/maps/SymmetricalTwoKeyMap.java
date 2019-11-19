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

package org.didelphis.structures.maps;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import org.didelphis.structures.contracts.SymmetricallyAccessible;
import org.didelphis.structures.tuples.Triple;
import org.didelphis.structures.tuples.Tuple;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SymmetricalTwoKeyMap<K, V> extends GeneralTwoKeyMap<K, K, V>
		implements SymmetricallyAccessible<K> {

	/**
	 * Default constructor which uses a {@link HashMap} delegate
	 */
	public SymmetricalTwoKeyMap() {}

	/**
	 * Standard constructor, allows the user to specify which type of {@link
	 * Map} the two-key-map should use
	 *
	 * @param mapType the type of map used to construct the two-key-map
	 */
	@SuppressWarnings ("rawtypes")
	public SymmetricalTwoKeyMap(Class<? extends Map> mapType) {
		super(mapType);
	}

	/**
	 * Copy constructor, allows the user to specify which type of {@link Map}
	 * the two-key-map should use, and what data to copy into this instance
	 *
	 * @param mapType the type of map used to construct the two-key-map
	 * @param iterable the data to copy into the new instance
	 */
	@SuppressWarnings ("rawtypes")
	public SymmetricalTwoKeyMap(
			@NonNull Class<? extends Map> mapType,
			@NonNull Iterable<Triple<K, K, V>> iterable
	) {
		super(mapType, iterable);
	}

	@Override
	public @Nullable V get(@Nullable K k1, @Nullable K k2) {
		Tuple<K, K> tuple = canonicalKeyPair(k1, k2);
		return super.get(tuple.getLeft(), tuple.getRight());
	}

	@Override
	public void put(@Nullable K k1, @Nullable K k2, @Nullable V value) {
		Tuple<K, K> tuple = canonicalKeyPair(k1, k2);
		super.put(tuple.getLeft(), tuple.getRight(), value);
	}

	@Override
	public boolean contains(@Nullable K k1, @Nullable K k2) {
		Tuple<K, K> tuple = canonicalKeyPair(k1, k2);
		return super.contains(tuple.getLeft(), tuple.getRight());
	}
}
