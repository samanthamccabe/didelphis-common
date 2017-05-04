package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.contracts.SymmetricallyAccessible;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Map;

/**
 * Created by samantha on 1/15/17.
 */
public class SymmetricalTwoKeyMap<K, V> 
		extends GeneralTwoKeyMap<K,K,V>
	implements SymmetricallyAccessible<K> {

	private static final int HASH_ID = 0x4B23275D;

	public SymmetricalTwoKeyMap() {}
	
	public SymmetricalTwoKeyMap(SymmetricalTwoKeyMap<K, V> map) {
		super(MapUtils.copyTwoKeyMap(map.getDelegate()));
	}

	public SymmetricalTwoKeyMap(Map<K, Map<K, V>> delegateMap) {
		super(delegateMap);
	}
	
	@Override
	public V get(K k1, K k2) {
		Tuple<K, K> tuple = canonicalKeyPair(k1, k2);
		return super.get(tuple.getLeft(), tuple.getRight());
	}

	@Override
	public void put(K k1, K k2, V value) {
		Tuple<K, K> tuple = canonicalKeyPair(k1, k2);
		super.put(tuple.getLeft(), tuple.getRight(), value);
	}

	@Override
	public boolean contains(K k1, K k2) {
		Tuple<K, K> tuple = canonicalKeyPair(k1, k2);
		return super.contains(tuple.getLeft(), tuple.getRight());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SymmetricalTwoKeyMap)) return false;
		SymmetricalTwoKeyMap<?, ?> that = (SymmetricalTwoKeyMap<?, ?>) o;
		return super.equals(that);
	}

	@Override
	public int hashCode() {
		return ~(HASH_ID ^ super.hashCode() << 1);
	}

	@Override
	public String toString() {
		return "SymmetricalTwoKeyMap{" + super.toString() + '}';
	}

}
