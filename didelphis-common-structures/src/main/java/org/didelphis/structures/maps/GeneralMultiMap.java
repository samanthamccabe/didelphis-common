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
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by samantha on 5/4/17.
 */
@SuppressWarnings("rawtypes")
public class GeneralMultiMap<K, V> implements MultiMap<K, V>,
		Delegating<Map<K,Collection<V>>> {

	private final Map<K, Collection<V>> delegate;
	
	private final Class<? extends Collection> type;
			
	public GeneralMultiMap() {
		delegate = new HashMap<>();
		//noinspection unchecked
		type = HashSet.class;
	}
	
	public GeneralMultiMap(
			@NotNull Map<K,Collection<V>> delegate,
			@NotNull Class<? extends Collection> type) {
		this.delegate = new HashMap<>(delegate);
		this.type = type;
	}

	public GeneralMultiMap(
			@NotNull Delegating<Map<K,Collection<V>>> delegating,
			@NotNull Class<? extends Collection> type) {
		delegate = MapUtils.copyMultiMap(delegating.getDelegate(), type);
		this.type=type;
	}

	@Override
	public Collection<V> get(K key) {
		return delegate.get(key);
	}

	@Override
	public boolean containsKey(K key) {
		return delegate.containsKey(key);
	}

	@NotNull
	@Override
	public Collection<K> keys() {
		return delegate.keySet();
	}

	@Override
	public Collection<V> remove(K key) {
		return delegate.remove(key);
	}

	@Override
	public void add(K key, V value) {
		if (delegate.containsKey(key)) {
			delegate.get(key).add(value);
		} else {
			Collection<V> set = MapUtils.newCollection(type);
			set.add(value);
			delegate.put(key, set);
		}
	}

	@Override
	public void addAll(K key, @NotNull Collection<V> values) {
		if (delegate.containsKey(key)) {
			delegate.get(key).addAll(values);
		} else {
			delegate.put(key, values);
		}
	}

	@NotNull
	@Override
	public Map<K, Collection<V>> getDelegate() {
		return delegate;
	}

	@Override
	public int size() {
		return delegate.values().stream().mapToInt(Collection::size).sum();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean clear() {
		boolean hasContent = !isEmpty();
		delegate.clear();
		return hasContent;
	}

	@NotNull
	@Override
	public Iterator<Tuple<K, Collection<V>>> iterator() {
		return delegate.entrySet().stream()
				.map(entry -> (Tuple<K,Collection<V>>) new Couple<>(
						entry.getKey(), entry.getValue()))
				.collect(Collectors.toList()).iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GeneralMultiMap)) return false;
		GeneralMultiMap<?, ?> that = (GeneralMultiMap<?, ?>) o;
		return Objects.equals(delegate, that.delegate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(delegate);
	}

	@NotNull
	@Override
	public String toString() {
		return "GeneralMultiMap{" + "delegate=" + delegate + '}';
	}
}
