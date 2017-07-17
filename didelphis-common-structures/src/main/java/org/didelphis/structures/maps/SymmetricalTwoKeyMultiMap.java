/*=============================================================================
 = Copyright (c) 2017. Samantha Fiona McCabe (Didelphis)
 =
 = Licensed under the Apache License, Version 2.0 (the "License");
 = you may not use this file except in compliance with the License.
 = You may obtain a copy of the License at
 =     http://www.apache.org/licenses/LICENSE-2.0
 = Unless required by applicable law or agreed to in writing, software
 = distributed under the License is distributed on an "AS IS" BASIS,
 = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 = See the License for the specific language governing permissions and
 = limitations under the License.
 =============================================================================*/

package org.didelphis.structures.maps;

import org.didelphis.structures.contracts.Delegating;
import org.didelphis.structures.maps.interfaces.TwoKeyMultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by samantha on 4/25/17.
 */
@SuppressWarnings("rawtypes")
public class SymmetricalTwoKeyMultiMap<K, V> 
		extends SymmetricalTwoKeyMap<K, Collection<V>>
		implements TwoKeyMultiMap<K, K, V> {
	
	private static final int HASH_ID = 0xd2a5dbb1;
	private final Class<? extends Collection> type;

	public SymmetricalTwoKeyMultiMap() {
		//noinspection unchecked
		type = HashSet.class;
	}
	
	public SymmetricalTwoKeyMultiMap(
			@NotNull Delegating<Map<K, Map<K, Collection<V>>>> delegating,
			@NotNull Class<? extends Collection> type
	) {
		super(delegating);
		this.type = type;
	}

	public SymmetricalTwoKeyMultiMap(
			@NotNull Map<K, Map<K, Collection<V>>> delegateMap,
			@NotNull Class<? extends Collection> type
	) {
		super(delegateMap);
		this.type = type;
	}
	
	@Override
	public void add(K k1, K k2, V value) {
		Collection<V> collection = get(k1, k2);
		if (collection != null) {
			collection.add(value);
		} else {
			Collection<V> set = MapUtils.newCollection(type);
			set.add(value);
			put(k1, k2, set);
		}
	}
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SymmetricalTwoKeyMultiMap<?,?> that = (SymmetricalTwoKeyMultiMap<?,?>) o;
		return super.equals(that);
	}

	@Override
	public int hashCode() {
		return ~(HASH_ID ^ super.hashCode() << 1);
	}

	@NotNull
	@Override
	public String toString() {
		return getClass().getName() + "-> {" + getDelegate() + '}';
	}
}
