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
import org.didelphis.structures.maps.interfaces.TwoKeyMap;
import org.didelphis.structures.tuples.Triple;
import org.didelphis.structures.tuples.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Created by samantha on 1/15/17.
 */
public class GeneralTwoKeyMap<T, U, V>
		implements TwoKeyMap<T, U, V>, Delegating<Map<T, Map<U, V>>> {

	private static final int HASH_ID = 0xb20ad93b;

	private final Map<T, Map<U, V>> delegate;
	
	@SuppressWarnings("rawtypes")
	private final Class<? extends Map> type;

	public GeneralTwoKeyMap() {
		delegate = new HashMap<>();
		//noinspection unchecked
		type = HashMap.class;
	}

	public GeneralTwoKeyMap(@NotNull Map<T, Map<U, V>> delegate) {
		this.delegate = delegate;
		//noinspection unchecked
		type = delegate.getClass();
	}

	public GeneralTwoKeyMap(@NotNull Delegating<Map<T, Map<U, V>>> delegating) {
		Map<T, Map<U, V>> map = delegating.getDelegate();

		//noinspection unchecked
		type = map.getClass();
		delegate = MapUtils.copyTwoKeyMap(map, type);
	}

	@Override
	public V get(T k1, U k2) {
		Map<U, V> map = delegate.get(k1);
		return (map == null) ? null : map.get(k2);
	}

	@Override
	public Collection<U> getAssociatedKeys(T k1) {
		return delegate.containsKey(k1) ? delegate.get(k1).keySet() : null;
	}
	
	@Override
	public void put(T k1, U k2, V value) {
		Map<U, V> map = delegate.containsKey(k1) 
		                ? delegate.get(k1) 
		                : MapUtils.newMap(type);
		map.put(k2, value);
		delegate.put(k1, map);
	}

	@Override
	public boolean contains(T k1, U k2) {
		return delegate.containsKey(k1) && delegate.get(k1).containsKey(k2);
	}

	@Override
	public Collection<Tuple<T, U>> keys() {
		Collection<Tuple<T, U>> keys = new ArrayList<>();
		for (Entry<T, Map<U, V>> entry : delegate.entrySet()) {
			T k1 = entry.getKey();
			for (U k2 : entry.getValue().keySet()) {
				keys.add(new Tuple<>(k1, k2));
			}
		}
		return keys;
	}

	@Override
	public V remove(T k1, U k2) {
		if (contains(k1, k2)) {
			return delegate.get(k1).remove(k2);
		}
		return null;
	}

	@Override
	public int size() {
		return delegate.values()
				.parallelStream()
				.mapToInt(map -> map.values().size())
				.sum();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean clear() {
		boolean wasCleared = !isEmpty();
		delegate.clear();
		return wasCleared;
	}

	@NotNull
	@Override
	public Iterator<Triple<T, U, V>> iterator() {
		Collection<Triple<T, U, V>> triples = new ArrayList<>();
		for (Entry<T, Map<U, V>> e1 : delegate.entrySet()) {
			T k1 = e1.getKey();
			for (Entry<U, V> e2 : e1.getValue().entrySet()) {
				triples.add(new Triple<>(k1, e2.getKey(), e2.getValue()));
			}
		}
		return triples.iterator();
	}

	@Override
	public int hashCode() {
		return HASH_ID * Objects.hash(delegate);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeneralTwoKeyMap<?, ?, ?> that = (GeneralTwoKeyMap<?, ?, ?>) o;
		return Objects.equals(delegate, that.delegate);
	}

	@Override
	public String toString() {
		return "GeneralTwoKeyMap{" + super.toString() + '}';
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@Override
	public Map<T, Map<U, V>> getDelegate() {
		return delegate;
	}
}
