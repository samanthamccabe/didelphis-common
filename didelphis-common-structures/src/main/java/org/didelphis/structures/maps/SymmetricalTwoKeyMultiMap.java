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
 * @author Samantha Fiona McCabe
 * @date 2017-04-25
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
	 * @param twoKeyMultiMap a symmetric two key map whose data is to be copied
	 * @param delegate a delegate map to be used by the new instance
	 * @param mSupplier a {@link Supplier} to provide the inner map instances
	 * @param cSupplier a {@link Supplier} to provide the inner collections
	 */
	public SymmetricalTwoKeyMultiMap(
			@NonNull SymmetricalTwoKeyMultiMap<K, V> twoKeyMultiMap,
			@NonNull Map<K, Map<K, Collection<V>>> delegate,
			@NonNull Supplier<? extends Map<K, Collection<V>>> mSupplier,
			@NonNull Supplier<? extends Collection<V>> cSupplier
	) {
		this(delegate,mSupplier,cSupplier);

		for (Triple<K, K, Collection<V>> triple : twoKeyMultiMap) {
			K k1 = triple.getFirstElement();
			K k2 = triple.getSecondElement();

			Collection<V> values = cSupplier.get();
			values.addAll(triple.getThirdElement());

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
