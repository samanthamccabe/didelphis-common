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

package org.didelphis.structures.maps.interfaces;

import lombok.NonNull;
import org.didelphis.structures.contracts.Streamable;
import org.didelphis.structures.tuples.Tuple;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Interface {@code MultiMap}
 *
 * A general specification for a multi-map, which maps a single key to a
 * collection of values.
 *
 * @param <K> the type of the map keys
 * @param <V> the type of the stored values; multiple such values are stored
 * 		per key
 *
 * @author Samantha Fiona McCabe
 * @date 2017-05-04
 * @see org.didelphis.structures.maps.GeneralMultiMap
 * @see org.didelphis.structures.maps.GeneralTwoKeyMultiMap
 * @since 0.1.0
 */
public interface MultiMap<K, V>
		extends Map<K, Collection<V>>, Streamable<Tuple<K, Collection<V>>> {

	/**
	 * Returns a collection of all keys used by the map
	 *
	 * @return a collection of all keys used by the map; cannot be null.
	 *
	 * @see Map#keySet()
	 */
	@NonNull
	Collection<K> keys();

	/**
	 * Inserts a new value to the structure associated with the provided key or
	 * creates a new structure containing the new value if no such key exists.
	 *
	 * @param key the key whose associated collection will have the new value
	 * 		appended; may be null
	 * @param value the value to be added to the set stored under these keys
	 *
	 * @throws NullPointerException if parameter {@code value} is null
	 */
	void add(@Nullable K key, @Nullable V value);

	/**
	 * Adds all of the provided values to those already present under the
	 * provided key. Implementations need no create the key if it does not
	 * exist, but it is recommended.
	 *
	 * @param key the key whose associated collection will have have the new
	 * 		values appended to it; may be null
	 * @param values the collection of values to be added to those present;
	 * 		cannot be null
	 *
	 * @throws NullPointerException if parameter {@code values} is null
	 * @see Map#putAll(Map)
	 */
	@Contract("_, null -> fail")
	void addAll(@Nullable K key, @NonNull Collection<V> values);
}
