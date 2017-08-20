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
import org.didelphis.structures.tuples.Triple;
import org.didelphis.structures.tuples.Tuple;

import java.util.Collection;

/**
 * Created by samantha on 1/15/17.
 */
public interface TwoKeyMap<T, U, V>
		extends Streamable<Triple<T, U, V>>, Structure {

	/**
	 * Return the value stored under the two keys
	 *
	 * @param k1 the first key; may be null
	 * @param k2 the second key; may be null
	 *
	 * @return the value stored under the given keys
	 */
	V get(T k1, U k2);

	/**
	 * Returns all keys associated with the provided key such that, together,
	 * they have an associated value
	 * @param k1 the first key; may be null
	 * @return a collection of the second keys associated with the provided key;
	 * null if the provided key is not present
	 */
	Collection<U> getAssociatedKeys(T k1);
	
	/**
	 * Inserts a new value under the two keys
	 *
	 * @param k1 the first key; may be null
	 * @param k2 the second key; may be null
	 * @param value the value to be inserted under the given keys
	 */
	void put(T k1, U k2, V value);

	/**
	 * Checks whether a value is present under the two keys
	 *
	 * @param k1 the first key; may be null
	 * @param k2 the second key; may be null
	 *
	 * @return true if the maps contains a value under the two keys
	 */
	boolean contains(T k1, U k2);

	/**
	 * @return a collection of tuples containing the maps's key pairs
	 */
	Collection<Tuple<T,U>> keys();

	/**
	 * Removes the value associated with the provided keys.
	 * @param k1 the first key; may be null
	 * @param k2 the second key; may be null
	 * @return the value associated with the provided key pair, if it exists; if
	 * no key pair exists, this operation will return null;
	 */
	V remove(T k1, U k2);

}
