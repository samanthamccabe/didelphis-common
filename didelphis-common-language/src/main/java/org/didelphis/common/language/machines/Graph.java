/******************************************************************************
 * Copyright (c) 2016. Samantha Fiona McCabe                                  *
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

package org.didelphis.common.language.machines;

import org.didelphis.common.structures.maps.TwoKeyMultiHashMap;
import org.didelphis.common.structures.maps.interfaces.TwoKeyMultiMap;
import org.didelphis.common.structures.tuples.Triple;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 1/28/2016
 */
public class Graph<T> implements TwoKeyMultiMap<String, T, String> {

	@Override
	public Set<String> get(String k1, T k2) {
		return map.get(k1, k2);
	}

	@Override
	public void put(String k1, T k2, Set<String> value) {
		map.put(k1, k2, value);
	}

	@Override
	public boolean contains(String k1, T k2) {
		return map.contains(k1, k2);
	}

	@Override
	public Collection<Tuple<String, T>> keys() {
		return map.keys();
	}

	@Override
	public void add(String k1, T k2, String value) {
		map.add(k1, k2, value);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Map<T, Set<String>> get(Object key) {
		return map.get(key);
	}

	@Override
	public Map<T, Set<String>> put(String key, Map<T, Set<String>> value) {
		return map.put(key, value);
	}

	@Override
	public Map<T, Set<String>> remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Map<T, Set<String>>> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<Map<T, Set<String>>> values() {
		return map.values();
	}

	@Override
	public Set<Entry<String, Map<T, Set<String>>>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Map<T, Set<String>> getOrDefault(Object key,
			Map<T, Set<String>> defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public void forEach(
			BiConsumer<? super String, ? super Map<T, Set<String>>> action) {
		map.forEach(action);
	}

	@Override
	public void replaceAll(
			BiFunction<? super String, ? super Map<T, Set<String>>, ? extends Map<T, Set<String>>> function) {
		map.replaceAll(function);
	}

	@Override
	public Map<T, Set<String>> putIfAbsent(String key,
			Map<T, Set<String>> value) {
		return map.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return map.remove(key, value);
	}

	@Override
	public boolean replace(String key, Map<T, Set<String>> oldValue,
			Map<T, Set<String>> newValue) {
		return map.replace(key, oldValue, newValue);
	}

	@Override
	public Map<T, Set<String>> replace(String key, Map<T, Set<String>> value) {
		return map.replace(key, value);
	}

	@Override
	public Map<T, Set<String>> computeIfAbsent(String key,
			Function<? super String, ? extends Map<T, Set<String>>> mappingFunction) {
		return map.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public Map<T, Set<String>> computeIfPresent(String key,
			BiFunction<? super String, ? super Map<T, Set<String>>, ? extends Map<T, Set<String>>> remappingFunction) {
		return map.computeIfPresent(key, remappingFunction);
	}

	@Override
	public Map<T, Set<String>> compute(String key,
			BiFunction<? super String, ? super Map<T, Set<String>>, ? extends Map<T, Set<String>>> remappingFunction) {
		return map.compute(key, remappingFunction);
	}

	@Override
	public Map<T, Set<String>> merge(String key, Map<T, Set<String>> value,
			BiFunction<? super Map<T, Set<String>>, ? super Map<T, Set<String>>, ? extends Map<T, Set<String>>> remappingFunction) {
		return map.merge(key, value, remappingFunction);
	}

	@Override
	public Iterator<Triple<String, T, Set<String>>> iterator() {
		return map.iterator();
	}

	@Override
	public void forEach(
			Consumer<? super Triple<String, T, Set<String>>> action) {
		map.forEach(action);
	}

	@Override
	public Spliterator<Triple<String, T, Set<String>>> spliterator() {
		return map.spliterator();
	}

	private final TwoKeyMultiMap<String, T, String> map;

	public Graph() {
		map = new TwoKeyMultiHashMap<>();
	}

	public Graph(Graph<T> graph) {
		map = new TwoKeyMultiHashMap<>(graph.map);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Graph)) return false;
		Graph<?> graph = (Graph<?>) o;
		return Objects.equals(map, graph.map);
	}

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	@Override
	public String toString() {
		return "Graph{" + "map=" + map + '}';
	}
}
