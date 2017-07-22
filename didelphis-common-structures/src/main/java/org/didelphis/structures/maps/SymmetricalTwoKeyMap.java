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
import org.didelphis.structures.contracts.SymmetricallyAccessible;
import org.didelphis.structures.tuples.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	
	@Nullable
	@Override
	public V get(K k1, K k2) {
		Tuple<K,K> tuple = canonicalKeyPair(k1, k2);
		return super.get(tuple.getLeft(), tuple.getRight());
	}

	@Override
	public void put(K k1, K k2, V value) {
		Tuple<K,K> tuple = canonicalKeyPair(k1, k2);
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
		Tuple<K,K> tuple = canonicalKeyPair(k1, k2);
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
