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

import org.didelphis.structures.Suppliers;
import org.didelphis.structures.maps.interfaces.TwoKeyMap;
import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Triple;
import org.didelphis.structures.tuples.Tuple;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
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
@ToString (of = "delegate")
@EqualsAndHashCode (of = "delegate")
public class GeneralTwoKeyMap<T, U, V> implements TwoKeyMap<T, U, V> {

	private final Map<T, Map<U, V>> delegate;
	private final Supplier<? extends Map<U, V>> mapSupplier;

	/**
	 * Default constructor which uses a {@link HashMap} delegate
	 */
	public GeneralTwoKeyMap() {
		delegate = new HashMap<>();
		mapSupplier = HashMap::new;
	}

	/**
	 * Standard constructor, allows the user to specify which type of {@link
	 * Map} the two-key-map should use
	 *
	 * @param mapType the type of map used to construct the two-key-map
	 */
	@SuppressWarnings ({"rawtypes", "unchecked"})
	public GeneralTwoKeyMap(@NonNull Class<? extends Map> mapType) {
		Class<? extends Map<?, ?>> type = (Class<? extends Map<?, ?>>) mapType;
		Supplier<? extends Map<?, ?>> supplier = Suppliers.mapOf(type);

		delegate = (Map<T, Map<U, V>>) supplier.get();
		mapSupplier = (Supplier<? extends Map<U, V>>) supplier;
	}

	/**
	 * Copy constructor, allows the user to specify which type of {@link Map}
	 * the two-key-map should use, and what data to copy into this instance
	 *
	 * @param mapType the type of map used to construct the two-key-map
	 * @param iterable the data to copy into the new instance
	 */
	@SuppressWarnings ("rawtypes")
	public GeneralTwoKeyMap(
			@NonNull Class<? extends Map> mapType,
			@NonNull Iterable<Triple<T, U, V>> iterable
	) {
		this(mapType);

		// Populate
		for (Triple<T, U, V> triple : iterable) {
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

	@Override
	public boolean containsFirstKey(@Nullable T k1) {
		return delegate.containsKey(k1);
	}

	@Override
	public boolean containsSecondKey(@Nullable U k2) {
		for (Map<U, V> uvMap : delegate.values()) {
			if (uvMap.containsKey(k2)) return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(@Nullable V value) {
		return false;
	}

	@NonNull
	@Override
	public Collection<Tuple<T, U>> keys() {
		Collection<Tuple<T, U>> keys = new ArrayList<>();
		for (Map.Entry<T, ? extends Map<U, V>> entry : delegate.entrySet()) {
			T k1 = entry.getKey();
			for (U k2 : entry.getValue().keySet()) {
				keys.add(new Couple<>(k1, k2));
			}
		}
		return keys;
	}

	@Override
	public int size() {
		return delegate.values().stream().mapToInt(Map::size).sum();
	}

	@Nullable
	@Override
	public V removeKeys(@Nullable T k1, @Nullable U k2) {
		return delegate.get(k1).remove(k2);
	}

	@Override
	public void putAll(@NonNull TwoKeyMap<T, U, V> map) {
		for (Triple<T, U, V> triple : map) {
			put(triple.first(), triple.second(), triple.third());
		}
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@NonNull
	@Override
	public Iterator<Triple<T, U, V>> iterator() {
		Collection<Triple<T, U, V>> triples = new ArrayList<>();
		for (Map.Entry<T, Map<U, V>> e1 : delegate.entrySet()) {
			T k1 = e1.getKey();
			for (Map.Entry<U, V> e2 : e1.getValue().entrySet()) {
				triples.add(new Triple<>(k1, e2.getKey(), e2.getValue()));
			}
		}
		return triples.iterator();
	}

	@NonNull
	protected final Map<T, Map<U, V>> getDelegate() {
		return delegate;
	}

	protected final Supplier<? extends Map<U, V>> getMapSupplier() {
		return mapSupplier;
	}
}
