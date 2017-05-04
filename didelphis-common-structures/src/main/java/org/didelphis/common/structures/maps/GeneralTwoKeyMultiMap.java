/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.contracts.Delegating;
import org.didelphis.common.structures.maps.interfaces.TwoKeyMultiMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 4/10/2016
 */
public class GeneralTwoKeyMultiMap<T, U, V> 
		extends GeneralTwoKeyMap<T, U, Collection<V>>
		implements TwoKeyMultiMap<T, U, V> {

	private static final int HASH_ID = 0x50de3d56;
	
	public GeneralTwoKeyMultiMap() {
	}

	public GeneralTwoKeyMultiMap(Map<T, Map<U, Collection<V>>> delegateMap) {
		super(delegateMap);
	}
	
	public GeneralTwoKeyMultiMap(GeneralTwoKeyMultiMap<T, U, V> map) {
		this(MapUtils.copyMultiMap(map.getDelegate()));
	}

	@Override
	public void add(T k1, U k2, V value) {
		if (contains(k1, k2)) {
			get(k1, k2).add(value);
		} else {
			Collection<V> set = new HashSet<>();
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
		return getClass().getName() + "{"+getDelegate()+"}";
	}
}
