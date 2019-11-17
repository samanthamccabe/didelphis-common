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
import lombok.experimental.Delegate;

import org.didelphis.structures.Suppliers;
import org.didelphis.structures.contracts.Delegating;
import org.didelphis.structures.maps.interfaces.TwoKeyMap;
import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Triple;
import org.didelphis.structures.tuples.Tuple;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Class {@code GeneralTwoKeyMap}
 *
 * A general implementation of the {@code TwoKeyMap} interface.
 *
 * @param <T> the first key type
 * @param <U> the second key type
 * @param <V> the value type
 *
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
public class GeneralTwoKeyMap<T, U, V>
		implements TwoKeyMap<T, U, V>, Delegating<Map<T, ? extends Map<U, V>>> {

	@Delegate(excludes = Size.class)
	private final Map<T, Map<U, V>> delegate;
	private final Supplier<? extends Map<U, V>> mapSupplier;

	/**
	 * Default constructor which uses a {@link HashMap} delegate
	 */
	public GeneralTwoKeyMap() {
		delegate = new HashMap<>();
		mapSupplier = Suppliers.ofHashMap();
	}

	/**
	 * Standard non-copying constructor which uses the provided delegate map and
	 * creates new entries using the provided supplier.
	 *
	 * @param delegate a delegate map to be used by the new multi-map
	 * @param mapSupplier a {@link Supplier} to provide the inner map
	 *      instances
	 */
	public GeneralTwoKeyMap(
			@NonNull Map<T, Map<U, V>> delegate,
			@NonNull Supplier<? extends Map<U, V>> mapSupplier
	) {
		this.delegate = delegate;
		this.mapSupplier = mapSupplier;
	}

	/**
	 * Copy-constructor; creates a deep copy of the provided multi-map using
	 * the provided suppliers
	 *
	 * @param tripleIterable a {@link TwoKeyMap} instance whose data is to be
	 *      copied
	 * @param delegate a new (typically empty) delegate map
	 * @param mapSupplier a {@link Supplier} to provide the inner collections
	 */
	public GeneralTwoKeyMap(
			@NonNull Iterable<Triple<T, U, V>> tripleIterable,
			@NonNull Map<T, Map<U, V>> delegate,
			@NonNull Supplier<? extends Map<U, V>> mapSupplier
	) {
		this(delegate, mapSupplier);

		for (Triple<T, U, V> triple : tripleIterable) {
			T k1 = triple.first();
			U k2 = triple.second();
			V value = triple.third();
			if (delegate.containsKey(k1)) {
				Map<U, V> map = delegate.get(k1);
				map.put(k2, value);
			} else {
				Map<U, V> map = mapSupplier.get();
				map.put(k2, value);
				delegate.put(k1, map);
			}
		}
	}

	@Override
	public @Nullable V get(@Nullable T k1, @Nullable U k2) {
		Map<U, V> map = delegate.get(k1);
		return (map == null) ? null : map.get(k2);
	}

	@Override
	public void put(@Nullable T k1, @Nullable U k2, @Nullable V value) {
		Map<U, V> map = delegate.containsKey(k1)
				? delegate.get(k1)
				: mapSupplier.get();
		map.put(k2, value);
		delegate.put(k1, map);
	}

	@Override
	public boolean contains(@Nullable T k1, @Nullable U k2) {
		return delegate.containsKey(k1) && delegate.get(k1).containsKey(k2);
	}

	@NonNull
	@Override
	public Collection<Tuple<T, U>> keys() {
		Collection<Tuple<T, U>> keys = new ArrayList<>();
		for (Entry<T, ? extends Map<U, V>> entry : delegate.entrySet()) {
			T k1 = entry.getKey();
			for (U k2 : entry.getValue().keySet()) {
				keys.add(new Couple<>(k1, k2));
			}
		}
		return keys;
	}

	@NonNull
	@Override
	public Collection<U> getAssociatedKeys(T k1) {
		return delegate.containsKey(k1)
				? delegate.get(k1).keySet()
				: Collections.emptySet();
	}

	@NonNull
	@Override
	public Iterator<Triple<T, U, V>> iterator() {
		Collection<Triple<T, U, V>> triples = new ArrayList<>();
		for (Entry<T, Map<U, V>> e1 : delegate.entrySet()) {
			T k1 = e1.getKey();
			for (Entry<U, V> e2 : e1.getValue().entrySet()) {
				triples.add(new Triple<>(k1, e2.getKey(), e2.getValue()));
			}
		}
		return triples.iterator();
	}

	@NonNull
	@Override
	public Map<T, Map<U, V>> getDelegate() {
		return delegate;
	}

	protected Supplier<? extends Map<U, V>> getMapSupplier() {
		return mapSupplier;
	}

	// A weird but necessary way of ensuring @Delegate works correctly; the
	// delegated .size() call from Map will only return the size of the outer
	// Map, but the designed behavior for a two-key map is that the size is the
	// total number of entries
	@SuppressWarnings({
			"InterfaceNeverImplemented",
			"InterfaceMayBeAnnotatedFunctional"
	})
	private interface Size {
		int size();
	}
}
