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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SymmetricalTwoKeyMap<K, V> extends GeneralTwoKeyMap<K, K, V>
		implements SymmetricallyAccessible<K> {

	/**
	 * Default constructor which uses a {@link HashMap} delegate
	 */
	public SymmetricalTwoKeyMap() {
	}

	/**
	 * Standard non-copying constructor which uses the provided delegate map and
	 * creates new entries using the provided supplier.
	 * @param delegate a delegate map to be used by the new multimap
	 * @param mapSupplier a {@link Supplier} to provide the inner map instances
	 */
	public SymmetricalTwoKeyMap(
			@NonNull Map<K, Map<K, V>> delegate,
			@NonNull Supplier<? extends Map<K, V>> mapSupplier
	) {
		super(delegate, mapSupplier);
	}

	/**
	 * Copy-constructor; creates a deep copy of the provided multi-map using
	 * the provided suppliers
	 *
	 * @param tripleIterable triples whose data is to be copied
	 * @param delegate a (typically empty) delegate map
	 * @param mapSupplier a {@link Supplier} to provide the inner map
	 */
	public SymmetricalTwoKeyMap(
			@NonNull Iterable<Triple<K, K, V>> tripleIterable,
			@NonNull Map<K, Map<K, V>> delegate,
			@NonNull Supplier<? extends Map<K, V>> mapSupplier
	) {
		super(tripleIterable, delegate, mapSupplier);
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

	@NonNull
	@Override
	public Collection<K> getAssociatedKeys(@Nullable K k1) {
		return keys().stream()
				.filter(tuple -> tuple.contains(k1))
				.map(tuple -> Objects.equals(tuple.getLeft(), k1)
						? tuple.getRight()
						: tuple.getLeft())
				.collect(Collectors.toSet());
	}
}
