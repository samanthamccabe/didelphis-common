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
import lombok.ToString;
import org.didelphis.structures.Suppliers;
import org.didelphis.structures.maps.interfaces.TwoKeyMultiMap;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Class {@code GeneralTwoKeyMultiMap}
 *
 * @author Samantha Fiona McCabe
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
	
	public GeneralTwoKeyMultiMap(
			@NonNull Map<T, Map<U, Collection<V>>> delegate,
			@NonNull Supplier<Map<U, Collection<V>>> mapSupplier,
			@NonNull Supplier<? extends Collection<V>> collectionSupplier
	) {
		super(delegate, mapSupplier);
		this.collectionSupplier = collectionSupplier;
	}
	
	public GeneralTwoKeyMultiMap(GeneralTwoKeyMultiMap<T, U, V> map) {
		this(MapUtils.copyTwoKeyMultiMap(map.getDelegate()),
				map.getMapSupplier(),
				map.collectionSupplier);
	}


	@Override
	public void add(T k1, U k2, @Nullable V value) {
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
