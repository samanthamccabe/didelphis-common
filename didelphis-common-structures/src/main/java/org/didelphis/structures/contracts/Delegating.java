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

package org.didelphis.structures.contracts;

import org.jetbrains.annotations.NotNull;

/**
 * Interface {@code Delegating}
 * <p>
 * Indicates that a data structure delegates some functionality to an inner
 * collection object and guarantees the structure is available through the API.
 *
 * @param <T> the type of the delegate object.
 *
 * @author Samantha Fiona McCabe
 * @date 2017-05-03
 * @since 0.1.0
 */
public interface Delegating<T> {

	/**
	 * Provides access to the delegate used by the implementing class
	 *
	 * @return the delegate object; this must not return null.
	 */
	@NotNull
	T getDelegate();
}
