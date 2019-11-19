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

package org.didelphis.structures;

import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * Utility Class {@code Suppliers}
 *
 * @since 0.1.0
 */
@ToString
@UtilityClass
public final class Suppliers {

	public <T> Supplier<Set<T>> ofHashSet() {
		return HashSet::new;
	}

	public <K, V> Supplier<Map<K, V>> ofHashMap() {
		return HashMap::new;
	}

	public <K, V> Supplier<Map<K, V>> ofLinkedHashMap() {
		return LinkedHashMap::new;
	}

	public <T> Supplier<NavigableSet<T>> ofTreeSet() {
		return TreeSet::new;
	}

	public <T> Supplier<List<T>> ofList() {
		return ArrayList::new;
	}

	public <K, V> Supplier<NavigableMap<K, V>> ofTreeMap() {
		return TreeMap::new;
	}

	public <K, V> Supplier<Map<K, V>> mapOf(
			@NonNull Class<? extends Map<?, ?>> type
	) {
		return new MapSupplier<>(type);
	}

	public <E> Supplier<Collection<E>> collectionOf(
			@NonNull Class<? extends Collection<?>> type
	) {
		return new CollectionSupplier<>(type);
	}

	private static final class CollectionSupplier<E>
			implements Supplier<Collection<E>> {

		private final Class<? extends Collection<?>> type;

		private CollectionSupplier(Class<? extends Collection<?>> type) {
			this.type = type;
		}

		@Override
		@SuppressWarnings ("unchecked")
		public Collection<E> get() {
			try {
				Constructor<? extends Collection<?>> constructor =
						type.getConstructor();
				return (Collection<E>) constructor.newInstance();
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("Unable to create new two " +
						"key map using the provided class; it does not have " +
						"a default constructor.", e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Unable to create new two " +
						"key map using the provided class; it does not have " +
						"access to the class definition.", e);
			} catch (InstantiationException e) {
				throw new IllegalArgumentException("Unable to create new two " +
						"key map using the provided class; it cannot be " +
						"instantiated.", e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException("Unable to create new two " +
						"key map using the provided class; an error occurred " +
						"while instantiating it.", e);
			}
		}
	}

	private static final class MapSupplier<K, V>
			implements Supplier<Map<K, V>> {

		private final Class<? extends Map<?, ?>> type;

		private MapSupplier(Class<? extends Map<?, ?>> type) {
			this.type = type;
		}

		@Override
		@SuppressWarnings ("unchecked")
		public Map<K, V> get() {
			try {
				Constructor<? super Map<?, ?>> constructor =
						(Constructor<? super Map<?, ?>>) type.getConstructor();
				return (Map<K, V>) constructor.newInstance();
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("Unable to create new two " +
						"key map using the provided class; it does not have " +
						"a default constructor.", e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Unable to create new two " +
						"key map using the provided class; it does not have " +
						"access to the class definition.", e);
			} catch (InstantiationException e) {
				throw new IllegalArgumentException("Unable to create new two " +
						"key map using the provided class; it cannot be " +
						"instantiated.", e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException("Unable to create new two " +
						"key map using the provided class; an error occurred " +
						"while instantiating it.", e);
			}
		}
	}
}
