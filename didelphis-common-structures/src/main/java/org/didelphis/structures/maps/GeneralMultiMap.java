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
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Tuple;
import org.didelphis.utilities.Templates;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Class {@code GeneralMultiMap}
 *
 * A general-purpose multi-map, associating a single key with a collection of
 * values.
 *
 * @param <K> the key type
 * @param <V> the value type.
 *
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
public class GeneralMultiMap<K, V>
		implements MultiMap<K, V>, Delegating<Map<K, ? extends Collection<V>>> {

	private static final GeneralMultiMap<?, ?> EMPTY = new GeneralMultiMap<>(
			Collections.emptyMap(),
			() -> { String message = Templates.create()
					.add("Attempting to modify an immutable, empty instance of",
							"class {}")
					.with(GeneralMultiMap.class)
					.build();
			throw new UnsupportedOperationException(message);
			}
	);

	@SuppressWarnings("unchecked")
	public static <K, V> GeneralMultiMap<K,V> emptyMultiMap() {
		return (GeneralMultiMap<K, V>) EMPTY;
	}

	@Delegate
	private final Map<K, Collection<V>> delegate;
	private final Supplier<? extends Collection<V>> supplier;

	/**
	 * Default constructor which uses {@link HashMap} and {@link HashSet}
	 */
	public GeneralMultiMap() {
		delegate = new HashMap<>();
		supplier = Suppliers.ofHashSet();
	}

	/**
	 * Standard non-copying constructor which uses the provided delegate map and
	 * creates new entries using the provided supplier.
	 * @param delegate a delegate map to be used by the new multimap
	 * @param supplier a {@link Supplier} to provide the inner collections
	 */
	public GeneralMultiMap(
			@NonNull Map<K, Collection<V>> delegate,
			@NonNull Supplier<? extends Collection<V>> supplier
	) {
		this.delegate = delegate;
		this.supplier = supplier;
	}

	/**
	 * Copy-constructor; creates a deep copy of the provided multi-map using
	 * the provided suppliers
	 *
	 * @param tupleIterable an instance whose data is to be copied
	 * @param delegate a new (typically empty) delegate map
	 * @param supplier a {@link Supplier} to provide the inner collections
	 */
	public GeneralMultiMap(
			@NonNull Iterable<Tuple<K, Collection<V>>> tupleIterable,
			@NonNull Map<K, Collection<V>> delegate,
			@NonNull Supplier<? extends Collection<V>> supplier
	) {
		this.delegate = delegate;
		this.supplier = supplier;
		for (Tuple<K, Collection<V>> tuple : tupleIterable) {
			Collection<V> collection = supplier.get();
			collection.addAll(Objects.requireNonNull(tuple.getRight()));
			delegate.put(tuple.getLeft(), collection);
		}
	}

	@NonNull
	@Override
	public Collection<K> keys() {
		return delegate.keySet();
	}

	@Override
	public void add(@Nullable K key, @Nullable V value) {
		if (delegate.containsKey(key)) {
			delegate.get(key).add(value);
		} else {
			Collection<V> set = supplier.get();
			set.add(value);
			delegate.put(key, set);
		}
	}

	@Override
	public void addAll(@Nullable K key, @NonNull Collection<V> values) {
		if (delegate.containsKey(key)) {
			delegate.get(key).addAll(values);
		} else {
			delegate.put(key, values);
		}
	}

	@NonNull
	@Override
	public Map<K, Collection<V>> getDelegate() {
		return delegate;
	}

	@NonNull
	@Override
	public Iterator<Tuple<K, Collection<V>>> iterator() {
		return delegate.entrySet()
				.stream()
				.map(this::toTuple)
				.collect(Collectors.toList())
				.iterator();
	}

	@NonNull
	private Tuple<K, Collection<V>> toTuple(
			@NonNull Entry<? extends K, Collection<V>> entry
	) {
		return new Couple<>(entry.getKey(), entry.getValue());
	}
}
