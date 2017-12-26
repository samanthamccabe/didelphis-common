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

package org.didelphis.structures;

import lombok.AccessLevel;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

/**
 * Utility Class {@code Suppliers}
 *
 * @author Samantha Fiona McCabe
 * @date 2017-09-07
 * @since 0.1.0
 */
@SuppressWarnings("unchecked")
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@UtilityClass
@Slf4j
public final class Suppliers {

	/* --------------------------------------------------------------------- <*/
	Supplier<? extends List<?>>   ARRAY_LIST      = () -> new ArrayList<>();

	Supplier<? extends Set<?>>    HASH_SET        = () -> new HashSet<>();
	Supplier<? extends Map<?, ?>> HASH_MAP        = () -> new HashMap<>();
	Supplier<? extends Map<?, ?>> LINKED_HASH_MAP = () -> new LinkedHashMap<>();
	// Navigable
	Supplier<? extends NavigableSet<?>>    TREE_SET = () -> new TreeSet<>();
	Supplier<? extends NavigableMap<?, ?>> TREE_MAP = () -> new TreeMap<>();
	/*> --------------------------------------------------------------------- */

	public <T> Supplier<Set<T>> ofHashSet() {
		return (Supplier<Set<T>>) HASH_SET;
	}

	public <K, V> Supplier<Map<K, V>> ofHashMap() {
		return (Supplier<Map<K, V>>) HASH_MAP;
	}

	public <K, V> Supplier<Map<K, V>> ofLinkedHashMap() {
		return (Supplier<Map<K, V>>) LINKED_HASH_MAP;
	}

	public <T> Supplier<NavigableSet<T>> ofTreeSet() {
		return (Supplier<NavigableSet<T>>) TREE_SET;
	}

	public <T> Supplier<List<T>> ofList() {
		return (Supplier<List<T>>) ARRAY_LIST;
	}

	public <K, V> Supplier<NavigableMap<K, V>> ofTreeMap() {
		return (Supplier<NavigableMap<K, V>>) TREE_MAP;
	}
	
	public <K, V, C extends Map<K, V>> Supplier<C> mapOfType(Class<C> type) {
		return () -> instantiate(type);
	}

	public <T, C extends Collection<T>> Supplier<C> setOfType(Class<C> type) {
		return () -> instantiate(type);
	}

	public <T, C extends Collection<T>> Supplier<C> listOfType(Class<C> type) {
		return () -> instantiate(type);
	}

	/**
	 * Reflectively creates a new instance of the same type as the provided map.
	 * Note that the new instance will be empty.
	 * @param map
	 * @param <K>
	 * @param <V>
	 * @param <M>
	 * @return
	 */
	public <K, V, M extends Map<K, V>> M copyMap(M map) {
		return instantiate((Class<M>) map.getClass());
	}

	public <T, C extends Collection<T>> C copyCollection(C collection) {
		return instantiate((Class<C>) collection.getClass());
	}

	private <C> C instantiate(Class<C> type) {
		try {
			return type.getConstructor().newInstance();
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("No-arg constructor unavailable "
					+ "for type", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("The no-arg constructor for this"
					+ " type is not accessible to the calling method.", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("This class may not be "
					+ "instantiated", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
