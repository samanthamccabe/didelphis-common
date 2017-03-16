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

import org.didelphis.common.structures.maps.interfaces.TwoKeyMap;
import org.didelphis.common.structures.maps.interfaces.TwoKeyMultiMap;

import java.util.HashSet;
import java.util.Set;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 4/10/2016
 */
public class TwoKeyMultiHashMap<T, U, V>
		extends TwoKeyHashMap<T, U, Set<V>>
		implements TwoKeyMultiMap<T, U, V> {
	
	public TwoKeyMultiHashMap() {}
	
	public TwoKeyMultiHashMap(TwoKeyMap<T, U, Set<V>> map) {
		super(map);
	}
	
	@Override
	public void add(T k1, U k2, V value) {
		if (contains(k1,k2)) {
			get(k1,k2).add(value);
		} else {
			Set<V> set = new HashSet<>();
			set.add(value);
			put(k1,k2,set);
		}
	}
}
