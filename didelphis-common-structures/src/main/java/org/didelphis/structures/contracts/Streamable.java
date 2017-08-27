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

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Class {@code Streamable}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 * 		@date 2017-07-28
 */
public interface Streamable<E> extends Iterable<E> {

	@NotNull
	default Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	@NotNull
	default Stream<E> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}
}
