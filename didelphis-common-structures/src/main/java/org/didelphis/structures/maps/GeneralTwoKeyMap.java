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
 * @param <T> the first key type
 * @param <U> the second key type
 * @param <V> the value type
 *
 * @date 2017 -01-15
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
	 * @param delegate a delegate map to be used by the new multimap
	 * @param mapSupplier a {@link Supplier} to provide the inner map instances
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
	 * @param twoKeyMap a {@link TwoKeyMap} instance whose data is to be copied
	 * @param delegate a new (typically empty) delegate map
	 * @param mapSupplier a {@link Supplier} to provide the inner collections
	 */
	public GeneralTwoKeyMap(
			@NonNull TwoKeyMap<T, U, V> twoKeyMap,
			@NonNull Map<T, Map<U, V>> delegate,
			@NonNull Supplier<? extends Map<U, V>> mapSupplier
	) {
		this(delegate, mapSupplier);

		for (Triple<T, U, V> triple : twoKeyMap) {
			T k1 = triple.getFirstElement();
			U k2 = triple.getSecondElement();
			V value = triple.getThirdElement();
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
