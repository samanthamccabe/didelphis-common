package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.contracts.Delegating;
import org.didelphis.common.structures.maps.interfaces.MultiMap;
import org.didelphis.common.structures.tuples.Tuple;

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
public class GeneralMultiMap<K, V>
		implements MultiMap<K, V>, Delegating<Map<K,Collection<V>>> {

	private final Map<K,Collection<V>> delegate;
	
	public GeneralMultiMap() {
		delegate = new HashMap<>();
	}
	
	public GeneralMultiMap(Map<K,Collection<V>> delegate) {
		this.delegate = new HashMap<>(delegate);
	}

	public GeneralMultiMap(MultiMap<K, V> map) {
		this();
		for (K key : map.keys()) {
			delegate.put(key, map.get(key));
		}
	}

	@Override
	public Collection<V> get(K key) {
		return delegate.get(key);
	}

	@Override
	public boolean containsKey(K key) {
		return delegate.containsKey(key);
	}

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
			Collection<V> set = new HashSet<>();
			set.add(value);
			delegate.put(key, set);
		}
	}

	@Override
	public void addAll(K key, Collection<V> values) {
		if (delegate.containsKey(key)) {
			delegate.get(key).addAll(values);
		} else {
			delegate.put(key, values);
		}
	}

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

	@Override
	public Iterator<Tuple<K, Collection<V>>> iterator() {
		return delegate.entrySet().parallelStream()
				.map(entry -> new Tuple<>(entry.getKey(), entry.getValue()))
				.collect(Collectors.toSet()).iterator();
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

	@Override
	public String toString() {
		return "GeneralMultiMap{" + "delegate=" + delegate + '}';
	}
}
