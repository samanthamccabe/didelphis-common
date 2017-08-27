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

package org.didelphis.structures.maps.interfaces;

import org.didelphis.structures.Structure;
import org.didelphis.structures.contracts.Streamable;
import org.didelphis.structures.tuples.Tuple;

import java.util.Collection;

/**
 * Created by samantha on 5/4/17.
 */
public interface MultiMap<K, V>
		extends Streamable<Tuple<K, Collection<V>>>, Structure {

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
