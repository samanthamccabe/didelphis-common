package org.didelphis.common.structures.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by samantha on 4/30/17.
 */
public final class MapUtils {
	
	private MapUtils() {}

	public static <T, U, V> Map<T, Map<U, Collection<V>>> copyMultiMap(
			Map<T, Map<U, Collection<V>>> map) {
		Map<T, Map<U, Collection<V>>> map1 = new HashMap<>();
		for (Map.Entry<T, Map<U, Collection<V>>> e1 : map.entrySet()) {
			T key = e1.getKey();
			Map<U, Collection<V>> map2 = new HashMap<>();
			for (Map.Entry<U, Collection<V>> e2 : e1.getValue().entrySet()) {
				map2.put(e2.getKey(), new HashSet<>(e2.getValue()));
			}
			map1.put(key, map2);
		}
		return map1;
	}

	public static <T, U, V> Map<T, Map<U, V>> copyTwoKeyMap(
			Map<T, Map<U, V>> map) {
		Map<T, Map<U, V>> hashMap = new HashMap<>();
		for (Map.Entry<T, Map<U, V>> entry : map.entrySet()) {
			hashMap.put(entry.getKey(), new HashMap<>(entry.getValue()));
		}
		return hashMap;
	}
}
