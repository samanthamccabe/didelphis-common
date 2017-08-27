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

import java.util.Collection;

/**
 * @author Samantha Fiona McCabe
 * @date 4/10/2016
 */
public interface TwoKeyMultiMap<T, U, V> extends TwoKeyMap<T, U, Collection<V>> {

	/**
	 * Inserts a new value under the two keys
	 *
	 * @param k1 the first key; may be null
	 * @param k2 the second key; may be null
	 * @param value the value to be added to the set stored under these keys
	 */
	void add(T k1, U k2, V value);
}
