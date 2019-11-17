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
import org.didelphis.structures.maps.interfaces.TwoKeyMultiMap;
import org.didelphis.structures.tuples.Triple;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Class {@code SymmetricalTwoKeyMultiMap}
 *
 * @since 0.1.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SymmetricalTwoKeyMultiMap<K, V>
		extends SymmetricalTwoKeyMap<K, Collection<V>>
		implements TwoKeyMultiMap<K, K, V> {

	private final Supplier<? extends Collection<V>> collectionSupplier;

	/**
	 * Default constructor which uses {@link HashMap} and {@link HashSet}
	 */
	public SymmetricalTwoKeyMultiMap() {
		collectionSupplier = Suppliers.ofHashSet();
	}

	/**
	 * Copy-constructor; creates a deep copy of the map using the provided
	 * delegate and suppliers.
	 *
	 * @param tripleIterable triples whose data is to be copied
	 * @param delegate a delegate map to be used by the new instance
	 * @param mSupplier a {@link Supplier} to provide the inner map instances
	 * @param cSupplier a {@link Supplier} to provide the inner collections
	 */
	public SymmetricalTwoKeyMultiMap(
			@NonNull Iterable<Triple<K, K, Collection<V>>> tripleIterable,
			@NonNull Map<K, Map<K, Collection<V>>> delegate,
			@NonNull Supplier<? extends Map<K, Collection<V>>> mSupplier,
			@NonNull Supplier<? extends Collection<V>> cSupplier
	) {
		this(delegate,mSupplier,cSupplier);

		for (Triple<K, K, Collection<V>> triple : tripleIterable) {
			K k1 = triple.first();
			K k2 = triple.second();

			Collection<V> values = cSupplier.get();
			values.addAll(triple.third());

			if (delegate.containsKey(k1)) {
				delegate.get(k1).put(k2, values);
			} else {
				Map<K, Collection<V>> map = mSupplier.get();
				map.put(k2, values);
				delegate.put(k1, map);
			}
		}
	}

	public SymmetricalTwoKeyMultiMap(
			@NonNull Map<K, Map<K, Collection<V>>> delegate,
			@NonNull Supplier<? extends Map<K, Collection<V>>> mSupplier,
			@NonNull Supplier<? extends Collection<V>> cSupplier
	) {
		super(delegate, mSupplier);
		collectionSupplier = cSupplier;
	}

	@Override
	public void add(@Nullable K k1, @Nullable K k2, @Nullable V value) {
		Collection<V> collection = get(k1, k2);
		if (collection != null) {
			collection.add(value);
		} else {
			Collection<V> set = collectionSupplier.get();
			set.add(value);
			put(k1, k2, set);
		}
	}
}
