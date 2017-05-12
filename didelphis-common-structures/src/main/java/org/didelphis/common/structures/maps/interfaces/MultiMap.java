package org.didelphis.common.structures.maps.interfaces;

import org.didelphis.common.structures.Structure;
import org.didelphis.common.structures.tuples.Tuple;

import java.util.Collection;

/**
 * Created by samantha on 5/4/17.
 */
public interface MultiMap<K, V> 
		extends Iterable<Tuple<K, Collection<V>>>, Structure {

	Collection<V> get(K key);
	
	boolean containsKey(K key);
	
	Collection<K> keys();
	
	Collection<V> remove(K key);
	
	/**
	 * Inserts a new value to the structure associated with the provided key or
	 * creates a new structure containing the new value if no such key exists.
	 *
	 * @param key the key whose associated collection will have the new value 
	 * appended
	 * @param value the value to be added to the set stored under these keys
	 */
	void add(K key, V value);
	
	void addAll(K key, Collection<V> values);
	
}
