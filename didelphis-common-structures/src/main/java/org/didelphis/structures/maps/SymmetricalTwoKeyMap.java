/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
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

package org.didelphis.structures.maps;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.didelphis.structures.contracts.SymmetricallyAccessible;
import org.didelphis.structures.tuples.Tuple;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by samantha on 1/15/17.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SymmetricalTwoKeyMap<K, V> extends GeneralTwoKeyMap<K, K, V>
		implements SymmetricallyAccessible<K> {

	public SymmetricalTwoKeyMap() {
	}

	public SymmetricalTwoKeyMap(
			@NonNull Map<K, Map<K, V>> twoKeyMap,
			@NonNull Supplier<Map<K, V>> mapSupplier
	) {
		super(twoKeyMap, mapSupplier);
	}

	public SymmetricalTwoKeyMap(@NonNull SymmetricalTwoKeyMap<K, V> twoKeyMap) {
		super(twoKeyMap);
	}

	@Override
	public @Nullable V get(@Nullable K k1, @Nullable K k2) {
		Tuple<K, K> tuple = canonicalKeyPair(k1, k2);
		return super.get(tuple.getLeft(), tuple.getRight());
	}

	@Override
	public void put(@Nullable K k1, @Nullable K k2, @Nullable V value) {
		Tuple<K, K> tuple = canonicalKeyPair(k1, k2);
		super.put(tuple.getLeft(), tuple.getRight(), value);
	}

	@Override
	public boolean contains(@Nullable K k1, @Nullable K k2) {
		Tuple<K, K> tuple = canonicalKeyPair(k1, k2);
		return super.contains(tuple.getLeft(), tuple.getRight());
	}

	@NonNull
	@Override
	public Collection<K> getAssociatedKeys(@Nullable K k1) {
		return keys().stream()
				.filter(tuple -> tuple.contains(k1))
				.map(tuple -> Objects.equals(tuple.getLeft(), k1)
						? tuple.getRight()
						: tuple.getLeft())
				.collect(Collectors.toSet());
	}
}
