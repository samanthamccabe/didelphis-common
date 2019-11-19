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

import org.didelphis.structures.contracts.Delegating;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Tuple;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.didelphis.structures.Suppliers.*;

/**
 * Class {@code GeneralMultiMap}
 * <p>
 * A general-purpose multi-map, associating a single key with a collection of
 * values.
 *
 * @param <K> the key type
 * @param <V> the value type.
 *
 * @since 0.1.0
 */
@ToString (exclude = "mapSupplier")
@EqualsAndHashCode (exclude = "mapSupplier")
public class GeneralMultiMap<K, V>
		implements MultiMap<K, V>, Delegating<Map<K, ? extends Collection<V>>> {

	@Delegate
	private final Map<K, Collection<V>> delegate;
	private final Supplier<? extends Collection<V>> mapSupplier;

	/**
	 * Default constructor which uses {@link HashMap} and {@link HashSet}
	 */
	public GeneralMultiMap() {
		delegate = new HashMap<>();
		mapSupplier = HashSet::new;
	}

	/**
	 * Standard constructor, allowing the user to specify which {@link Map} and
	 * {@link Collection} implementations that the multi-map should use.
	 *
	 * @param mapType the type of map used to construct the multi-map
	 * @param collType the type of collection used to construct the multi-map
	 */
	@SuppressWarnings ({"unchecked", "rawtypes"})
	public GeneralMultiMap(
			@NonNull Class<? extends Map> mapType,
			@NonNull Class<? extends Collection> collType
	) {
		Class<? extends Map<?, ?>> type = (Class<? extends Map<?, ?>>) mapType;
		Supplier<? extends Map<?, ?>> supplier = mapOf(type);

		delegate = (Map<K, Collection<V>>) supplier.get();
		mapSupplier = collectionOf((Class<? extends Collection<?>>) collType);
	}

	/**
	 * Copy constructor, allowing the user to specify which {@link Map} and
	 * {@link Collection} implementations that the multi-map should use, and
	 * copy the contents of another multi-map
	 *
	 * @param mapType the type of map used to construct the multi-map
	 * @param collType the type of collection used to construct the multi-map
	 * @param map the data to be copied into the new instance
	 */
	@SuppressWarnings ("rawtypes")
	public GeneralMultiMap(
			@NonNull Class<? extends Map> mapType,
			@NonNull Class<? extends Collection> collType,
			@NonNull Map<K, Collection<V>> map
	) {
		this(mapType, collType);

		// Populate
		for (Entry<K, Collection<V>> entry : map.entrySet()) {
			Collection<V> collection = mapSupplier.get();
			collection.addAll(Objects.requireNonNull(entry.getValue()));
			delegate.put(entry.getKey(), collection);
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
			Collection<V> set = mapSupplier.get();
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
