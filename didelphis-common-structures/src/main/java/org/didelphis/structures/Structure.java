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

package org.didelphis.structures;

/**
 * This interface defines a general contract for a set of methods used in many
 * classes. It can be used to indicate some behavior found in
 * {@link java.util.Collection} but is more general.
 * 
 * Specifically, these are objects which can be said to have a size, and be
 * emptied of contents.
 * 
 * Implementing classes are very likely to be {@link Iterable} but
 * 
 * @author samantha.mccabe@didelphis.org
 */
public interface Structure {
	
	/**
	 * Returns the size of the structure, based on the total number of values or
	 * unique key pairs
	 * @return the number of values in this structure
	 */
	int size();

	/**
	 * Tests if the structure is empty.
	 * @return true iff {@code size > 0}
	 */
	boolean isEmpty();

	/**
	 * Deletes all contents from the structure.
	 * @return true iff contents were deleted; if the structure was already
	 * empty, this operation will return false.
	 */
	boolean clear();
}
