/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
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
