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

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.didelphis.structures.Suppliers;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility Class {@code MapUtils}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-04-30
 * @since 0.1.0
 */
@UtilityClass
public final class MapUtils {

	/**
	 * Produces a copy of the input map using the provided suppliers to generate
	 * objects of the types desired by the user.
	 *
	 * @param map the map whose contents are to be copied; not null
	 *
	 * @return a copy of the input map; not null
	 */
	@NonNull
	public <K, V> Map<K, Collection<V>> copyMultiMap(
			@NonNull Map<K, Collection<V>> map
	) {
		Map<K, Collection<V>> outerCopy = Suppliers.copyMap(map);
		for (Entry<K, Collection<V>> e1 : map.entrySet()) {
			Collection<V> collection = e1.getValue();
			Collection<V> collectionCopy = Suppliers.copyCollection(collection);
			collectionCopy.addAll(collection);
			outerCopy.put(e1.getKey(), collectionCopy);
		}
		return outerCopy;
	}

	/**
	 * Produces a copy of the input map using the provided suppliers to generate
	 * objects of the types desired by the user.
	 *
	 * @param map the map whose contents are to be copied; not null
	 *
	 * @return a copy of the input map; not null
	 */
	@NonNull
	public <T, U, V> Map<T, Map<U, Collection<V>>> copyTwoKeyMultiMap(
			@NonNull Map<T, Map<U, Collection<V>>> map
	) {
		Map<T, Map<U, Collection<V>>> outerCopy = Suppliers.copyMap(map);
		for (Entry<T, Map<U, Collection<V>>> e1 : map.entrySet()) {
			Map<U, Collection<V>> value = e1.getValue();
			Map<U, Collection<V>> innerCopy = Suppliers.copyMap(value);
			for (Entry<U, Collection<V>> e2 : value.entrySet()) {
				Collection<V> collection = e2.getValue();
				Collection<V> collectionCopy = Suppliers.copyCollection(
						collection);
				collectionCopy.addAll(collection);
				innerCopy.put(e2.getKey(), collectionCopy);
			}
			outerCopy.put(e1.getKey(), innerCopy);
		}
		return outerCopy;
	}

	/**
	 * Produces a copy of the input map using the provided suppliers to generate
	 * objects of the types desired by the user.
	 *
	 * @param map the map whose contents are to be copied; not null
	 *
	 * @return a copy of the input map; not null
	 */
	@NonNull
	public <T, U, V> Map<T, Map<U, V>> copyTwoKeyMap(
			@NonNull Map<T, Map<U, V>> map
	) {
		Map<T, Map<U, V>> outerCopy = Suppliers.copyMap(map);
		for (Entry<T, Map<U, V>> e1 : map.entrySet()) {
			Map<U, V> value = e1.getValue();
			Map<U, V> innerCopy = Suppliers.copyMap(value);
			for (Entry<U, V> e2 : value.entrySet()) {
				innerCopy.put(e2.getKey(), e2.getValue());
			}
			outerCopy.put(e1.getKey(), innerCopy);
		}
		return outerCopy;
	}
}
