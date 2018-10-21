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
import java.util.Map;
import java.util.function.Supplier;

/**
 * Class {@code GeneralTwoKeyMultiMap}
 *
 * @date 2016/04/10
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
	 * @param twoKeyMultiMap a two key map whose data is to be copied
	 * @param delegate a delegate map to be used by the new instance
	 * @param mSupplier a {@link Supplier} to provide the inner map instances
	 * @param cSupplier a {@link Supplier} to provide the inner collections
	 */
	public GeneralTwoKeyMultiMap(
			@NonNull TwoKeyMultiMap<T, U, V> twoKeyMultiMap,
			@NonNull Map<T, Map<U, Collection<V>>> delegate,
			@NonNull Supplier<? extends Map<U, Collection<V>>> mSupplier,
			@NonNull Supplier<? extends Collection<V>> cSupplier
	) {
		this(delegate, mSupplier, cSupplier);

		for (Triple<T, U, Collection<V>> triple : twoKeyMultiMap) {
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
