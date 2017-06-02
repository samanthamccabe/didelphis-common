package org.didelphis.common.structures.maps;

import org.didelphis.common.structures.contracts.Delegating;
import org.didelphis.common.structures.contracts.SymmetricallyAccessible;
import org.didelphis.common.structures.tuples.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by samantha on 1/15/17.
 */
public class SymmetricalTwoKeyMap<K, V> 
		extends GeneralTwoKeyMap<K,K,V>
	implements SymmetricallyAccessible<K> {

	private static final int HASH_ID = 0x4B23275D;

	public SymmetricalTwoKeyMap() {}

	public SymmetricalTwoKeyMap(@NotNull Map<K, Map<K, V>> map) {
		super(map);
	}
	
	public SymmetricalTwoKeyMap(@NotNull Delegating<Map<K, Map<K, V>>> delegating) {
		super(delegating);
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
	public Collection<K> getAssociatedKeys(K k1) {
		return keys().stream()
				.filter(tuple -> tuple.contains(k1))
				.map(tuple -> Objects.equals(tuple.getLeft(), k1)
				              ? tuple.getRight()
				              : tuple.getLeft())
				.collect(Collectors.toSet());
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
