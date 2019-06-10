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
import java.util.Map;
import java.util.function.Supplier;

/**
 * Class {@code GeneralTwoKeyMultiMap}
 *
 * @since 0.1.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GeneralTwoKeyMultiMap<T, U, V>
		extends GeneralTwoKeyMap<T, U, Collection<V>>
		implements TwoKeyMultiMap<T, U, V> {

	private final Supplier<? extends Collection<V>> collectionSupplier;

	public GeneralTwoKeyMultiMap() {
		collectionSupplier = Suppliers.ofHashSet();
	}

	/**
	 * Standard non-copying constructor which uses the provided delegate map and
	 * creates new entries using the provided suppliers.
	 * @param delegate a delegate map to be used by the new instance
	 * @param mSupplier a {@link Supplier} to provide the inner map instances
	 * @param cSupplier a {@link Supplier} to provide the inner collections
	 */
	public GeneralTwoKeyMultiMap(
			@NonNull Map<T, Map<U, Collection<V>>> delegate,
			@NonNull Supplier<? extends Map<U, Collection<V>>> mSupplier,
			@NonNull Supplier<? extends Collection<V>> cSupplier
	) {
		super(delegate, mSupplier);
		collectionSupplier = cSupplier;
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
	public GeneralTwoKeyMultiMap(
			@NonNull Iterable<Triple<T, U, Collection<V>>> tripleIterable,
			@NonNull Map<T, Map<U, Collection<V>>> delegate,
			@NonNull Supplier<? extends Map<U, Collection<V>>> mSupplier,
			@NonNull Supplier<? extends Collection<V>> cSupplier
	) {
		this(delegate, mSupplier, cSupplier);

		for (Triple<T, U, Collection<V>> triple : tripleIterable) {
			T k1 = triple.getFirstElement();
			U k2 = triple.getSecondElement();
			
			Collection<V> values = cSupplier.get();
			values.addAll(triple.getThirdElement());
			
			if (delegate.containsKey(k1)) {
				delegate.get(k1).put(k2, values);
			} else {
				Map<U, Collection<V>> map = mSupplier.get();
				map.put(k2, values);
				delegate.put(k1, map);
			}
		}
	}

	@Override
	public void add(T k1, @Nullable U k2, @Nullable V value) {
		Collection<V> collection = get(k1, k2);
		if (collection == null) {
			Collection<V> set = collectionSupplier.get();
			set.add(value);
			put(k1, k2, set);
		} else {
			collection.add(value);
		}
	}
}
