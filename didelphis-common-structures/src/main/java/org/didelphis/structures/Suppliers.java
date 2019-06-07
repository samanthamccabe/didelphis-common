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

import lombok.ToString;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
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
}
