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
import lombok.extern.slf4j.Slf4j;
import org.didelphis.structures.Suppliers;
import org.didelphis.structures.maps.interfaces.TwoKeyMultiMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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
@Slf4j
public class SymmetricalTwoKeyMultiMap<K, V>
		extends SymmetricalTwoKeyMap<K, Collection<V>>
		implements TwoKeyMultiMap<K, K, V> {

	private final Supplier<? extends Collection<V>> collectionSupplier;

	public SymmetricalTwoKeyMultiMap() {
		collectionSupplier = Suppliers.ofHashSet();
	}
	
	public SymmetricalTwoKeyMultiMap(@NonNull SymmetricalTwoKeyMultiMap<K, V> map) {
		this(MapUtils.copyTwoKeyMultiMap(map.getDelegate()),
				map.getMapSupplier() , 
				map.collectionSupplier);
	}

	public SymmetricalTwoKeyMultiMap(
			@NonNull Map<K, Map<K, Collection<V>>> delegateMap,
			@NonNull Supplier<Map<K, Collection<V>>> mapSupplier,
			@NonNull Supplier<? extends Collection<V>> collectionSupplier
	) {
		super(delegateMap, mapSupplier);
		this.collectionSupplier = collectionSupplier;
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
