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
import lombok.experimental.Delegate;
import org.didelphis.structures.Suppliers;
import org.didelphis.structures.contracts.Delegating;
import org.didelphis.structures.maps.interfaces.MultiMap;
import org.didelphis.structures.tuples.Couple;
import org.didelphis.structures.tuples.Tuple;
import org.didelphis.utilities.Exceptions;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Class {@code GeneralMultiMap}
 *
 * A general-purpose multi-map, associating a single key with a collection of
 * values.
 *
 * @param <K> the key type
 * @param <V> the value type.
 *
 * @author Samantha Fiona McCabe
 * @date 2017-05-04
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
public class GeneralMultiMap<K, V>
		implements MultiMap<K, V>, Delegating<Map<K, ? extends Collection<V>>> {

	private static final GeneralMultiMap<?, ?> EMPTY = new GeneralMultiMap<>(
			Collections.emptyMap(),
			() -> { throw Exceptions.unsupportedOperation()
					.add("Attempting to modify an immutable,",
							"empty instance of class {}")
					.with(GeneralMultiMap.class)
					.build(); }
	);
	
	@SuppressWarnings("unchecked")
	public static <K, V> GeneralMultiMap<K,V> emptyMultiMap() {
		return (GeneralMultiMap<K, V>) EMPTY;
	}
	
	@Delegate
	private final Map<K, Collection<V>> delegate;
	private final Supplier<? extends Collection<V>> supplier;

	public GeneralMultiMap() {
		delegate = new HashMap<>();
		supplier = Suppliers.ofHashSet();
	}

	public GeneralMultiMap(@NonNull GeneralMultiMap<K, V> multiMap) {
		this(MapUtils.copyMultiMap(multiMap.getDelegate()), multiMap.supplier);
	}
	
	public GeneralMultiMap(
			@NonNull Map<K, Collection<V>> delegate,
			@NonNull Supplier<? extends Collection<V>> supplier
	) {
		this.delegate = new HashMap<>(delegate);
		this.supplier = supplier;
	}

	@NonNull
	@Override
	public Collection<K> keys() {
		return delegate.keySet();
	}

	@Override
	public void add(@Nullable K key, @Nullable V value) {
		if (delegate.containsKey(key)) {
			delegate.get(key).add(value);
		} else {
			Collection<V> set = supplier.get();
			set.add(value);
			delegate.put(key, set);
		}
	}

	@Override
	public void addAll(@Nullable K key, @NonNull Collection<V> values) {
		if (delegate.containsKey(key)) {
			delegate.get(key).addAll(values);
		} else {
			delegate.put(key, values);
		}
	}

	@NonNull
	@Override
	public Map<K, Collection<V>> getDelegate() {
		return delegate;
	}

	@NonNull
	@Override
	public Iterator<Tuple<K, Collection<V>>> iterator() {
		return delegate.entrySet()
				.stream()
				.map(this::toTuple)
				.collect(Collectors.toList())
				.iterator();
	}

	@NonNull
	private Tuple<K, Collection<V>> toTuple(Entry<K, Collection<V>> entry) {
		return new Couple<>(entry.getKey(), entry.getValue());
	}
}
