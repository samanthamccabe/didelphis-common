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
@ToString (callSuper = true, exclude = "collectionSupplier")
@EqualsAndHashCode (callSuper = true, exclude = "collectionSupplier")
public class SymmetricalTwoKeyMultiMap<K, V>
		extends SymmetricalTwoKeyMap<K, Collection<V>>
		implements TwoKeyMultiMap<K, K, V> {

	private final Supplier<? extends Collection<V>> collectionSupplier;

	/**
	 * Default constructor which uses {@link HashMap} and {@link HashSet}
	 */
	public SymmetricalTwoKeyMultiMap() {
		collectionSupplier = HashSet::new;
	}

	/**
	 * General constructor, allowing the user to specify which {@link Map} and
	 * {@link Collection} implementations that the two-key multi-map should use
	 *
	 * @param mapType the type of map used to construct the two-key-map
	 * @param collType the type of collection used to construct the multi-map
	 */
	@SuppressWarnings ({"rawtypes", "unchecked"})
	public SymmetricalTwoKeyMultiMap(
			@NonNull Class<? extends Map> mapType,
			@NonNull Class<? extends Collection> collType
	) {
		super(mapType);
		Class<? extends Collection<?>> type =
				(Class<? extends Collection<?>>) collType;

		collectionSupplier = Suppliers.collectionOf(type);
	}

	/**
	 * Copy constructor, allowing the user to specify which {@link Map} and
	 * {@link Collection} implementations that the two-key multi-map should use,
	 * and copy the contents of another two-key multi-map
	 *
	 * @param mapType the type of map used to construct the multi-map
	 * @param collType the type of collection used to construct the multi-map
	 * @param iterable the data to be copied into the new instance
	 */
	@SuppressWarnings ({"rawtypes", "unchecked"})
	public SymmetricalTwoKeyMultiMap(
			@NonNull Class<? extends Map> mapType,
			@NonNull Class<? extends Collection> collType,
			@NonNull Iterable<Triple<K, K, Collection<V>>> iterable
	) {
		super(mapType);

		Class<? extends Collection<?>> type =
				(Class<? extends Collection<?>>) collType;
		collectionSupplier = Suppliers.collectionOf(type);

		// Populate
		Supplier<? extends Map<K, Collection<V>>> mSupplier = getMapSupplier();
		Map<K, Map<K, Collection<V>>> delegate = getDelegate();
		for (Triple<K, K, Collection<V>> triple : iterable) {
			K k1 = triple.first();
			K k2 = triple.second();

			Collection<V> values = collectionSupplier.get();
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
