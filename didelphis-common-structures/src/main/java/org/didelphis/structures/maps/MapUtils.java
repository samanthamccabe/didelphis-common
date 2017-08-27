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

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by samantha on 4/30/17.
 *
 * @date 2017-04-30
 */
@SuppressWarnings({"rawtypes", "unchecked", "JavaReflectionMemberAccess"})
@Slf4j
@UtilityClass
public final class MapUtils {

	/**
	 * Produces a copy of the input map using the provided suppliers to generate
	 * objects of the types desired by the user.
	 *
	 * @param map the map whose contents are to be copied; not null
	 * @param collectionType specifies the type of the collection; not null
	 *
	 * @return a copy of the input map; not null
	 */
	@NotNull
	public <K, V> Map<K, Collection<V>> copyMultiMap(
			@NotNull Map<K, Collection<V>> map,
			@NotNull Class<? extends Collection> collectionType
	) {

		Map<K, Collection<V>> map1 = newMap(map.getClass());
		for (Entry<K, Collection<V>> e : map.entrySet()) {
			K key = e.getKey();
			map1.put(key, copyCollection(collectionType, e.getValue()));
		}
		return map1;
	}

	/**
	 * Produces a copy of the input map using the provided suppliers to generate
	 * objects of the types desired by the user.
	 *
	 * @param map the map whose contents are to be copied; not null
	 * @param type specifies the type of the collection; not null
	 *
	 * @return a copy of the input map; not null
	 */
	@NotNull
	public <T, U, V> Map<T, Map<U, Collection<V>>> copyTwoKeyMultiMap(
			@NotNull Map<T, Map<U, Collection<V>>> map,
			@NotNull Class<? extends Collection> type
	) {

		Map<T, Map<U, Collection<V>>> map1 = newMap(map.getClass());
		for (Entry<T, Map<U, Collection<V>>> e1 : map.entrySet()) {
			T key = e1.getKey();

			Map<U, Collection<V>> map2 = newMap(map.getClass());
			for (Entry<U, Collection<V>> e2 : e1.getValue().entrySet()) {
				map2.put(e2.getKey(), copyCollection(type, e2.getValue()));
			}
			map1.put(key, map2);
		}
		return map1;
	}

	/**
	 * Produces a copy of the input map using the provided suppliers to generate
	 * objects of the types desired by the user.
	 *
	 * @param map the map whose contents are to be copied; not null
	 * @param type supplies a new map of the specified type; not null
	 *
	 * @return a copy of the input map; not null
	 */
	@NotNull
	public <T, U, V> Map<T, Map<U, V>> copyTwoKeyMap(
			@NotNull Map<T, Map<U, V>> map, @NotNull Class<? extends Map> type
	) {
		Map<T, Map<U, V>> map1 = newMap(map.getClass());
		for (Entry<T, Map<U, V>> entry : map.entrySet()) {
			map1.put(entry.getKey(), copyMap(type, entry.getValue()));
		}
		return map1;
	}

	@NotNull
	public <K, V> Map<K, V> newMap(@NotNull Class<? extends Map> type) {
		try {
			return (Map<K, V>) type.getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			log.warn("Unable to create instance of {}. "
					+ "Defaulting to HashMap", type, e);
		}
		return new HashMap<>();
	}

	@NotNull
	public <K, V> Map<K, V> copyMap(
			@NotNull Class<? extends Map> type, @NotNull Map<K, V> map
	) {
		try {
			return (Map<K, V>) type.getConstructor(map.getClass())
					.newInstance(map);
		} catch (ReflectiveOperationException e) {
			log.warn("Unable to create instance of {}. "
					+ "Defaulting to HashMap", type, e);
		}
		return new HashMap<>(map);
	}

	@NotNull
	public <E> Collection<E> newCollection(
			@NotNull Class<? extends Collection> type
	) {
		try {
			//noinspection unchecked
			return (Collection<E>) type.getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			log.warn("Unable to create instance of {}. "
					+ "Defaulting to HashMap", type, e);
		}
		return new HashSet<>();
	}

	@NotNull
	public <E> Collection<E> copyCollection(
			@NotNull Class<? extends Collection> type,
			@NotNull Collection<E> collection
	) {
		try {
			//noinspection unchecked
			return (Collection<E>) type.getConstructor(collection.getClass())
					.newInstance(collection);
		} catch (ReflectiveOperationException e) {
			log.warn("Unable to create instance of {}. "
					+ "Defaulting to HashMap", type, e);
		}
		return new HashSet<>(collection);
	}

}
