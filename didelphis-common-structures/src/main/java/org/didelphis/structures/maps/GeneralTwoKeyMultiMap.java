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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * @author Samantha Fiona McCabe
 * Date: 4/10/2016
 */
@SuppressWarnings("rawtypes")
public class GeneralTwoKeyMultiMap<T, U, V> 
		extends GeneralTwoKeyMap<T, U, Collection<V>>
		implements TwoKeyMultiMap<T, U, V> {

	private static final int HASH_ID = 0x50de3d56;
	
	private final Class<? extends Collection> type;

	public GeneralTwoKeyMultiMap() {
		//noinspection unchecked
		type = HashSet.class;
	}

	public GeneralTwoKeyMultiMap(
			@NotNull Map<T, Map<U, Collection<V>>> delegateMap,
			@NotNull Class<? extends Collection> type
	) {
		super(delegateMap);
		this.type = type;
	}
	
	public GeneralTwoKeyMultiMap(
			@NotNull Delegating<Map<T, Map<U, Collection<V>>>> map,
			@NotNull Class<? extends Collection> type
	) {
		super(MapUtils.copyTwoKeyMultiMap(map.getDelegate(), type));
		this.type = type;
	}

	@Override
	public void add(T k1, U k2, V value) {
		if (contains(k1, k2)) {
			get(k1, k2).add(value);
		} else {
			Collection<V> set = MapUtils.newCollection(type);
			set.add(value);
			put(k1, k2, set);
		}
	}
	
	@Override
	public int hashCode() {
		return HASH_ID * Objects.hash(getDelegate());
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof GeneralTwoKeyMultiMap)) return false;
		Delegating<?> map = (Delegating<?>) object;
		return getDelegate().equals(map.getDelegate());
	}
	
	@Override
	public String toString() {
		return getClass().getName() + '{' + getDelegate() + '}';
	}
}
