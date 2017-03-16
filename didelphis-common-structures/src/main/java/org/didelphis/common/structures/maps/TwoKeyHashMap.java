package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.maps.interfaces.TwoKeyMap;
import org.didelphis.common.structures.tuples.Triple;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by samantha on 1/15/17.
 */
public class TwoKeyHashMap<T,U,V>
		extends HashMap<T, Map<U,V>>
		implements TwoKeyMap<T, U, V> {
	
	public TwoKeyHashMap() {}
	
	protected TwoKeyHashMap(TwoKeyMap<T, U, V> map) {
		super(map);
	}
	
	@Override
	public V get(T k1, U k2) {
		Map<U, V> map = get(k1);
		return (map == null) ? null : map.get(k2);
	}

	@Override
	public void put(T k1, U k2, V value) {
		Map<U, V> map = containsKey(k1) ? get(k1) : new HashMap<>();
		map.put(k2, value);
		put(k1, map);
	}

	@Override
	public boolean contains(T k1, U k2) {
		return containsKey(k1) && get(k1).containsKey(k2);
	}

	@Override
	public Collection<Tuple<T, U>> keys() {
		Collection<Tuple<T,U>> keys = new HashSet<>();
		for (Entry<T, Map<U, V>> entry : entrySet()) {
			T k1 = entry.getKey();
			for (U k2 : entry.getValue().keySet()) {
				keys.add(new Tuple<>(k1, k2));
			}
		}
		return keys;
	}

	@Override
	public Iterator<Triple<T, U, V>> iterator() {
		Collection<Triple<T, U, V>> triples = new HashSet<>();
		for (Entry<T, Map<U, V>> e1 : entrySet()) {
			T k1 = e1.getKey();
			for (Entry<U, V> e2 : e1.getValue().entrySet()) {
				triples.add(new Triple<>(k1, e2.getKey(), e2.getValue()));
			}
		}
		return triples.iterator();
	}
}
