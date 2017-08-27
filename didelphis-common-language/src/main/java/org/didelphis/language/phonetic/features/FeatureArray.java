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

package org.didelphis.language.phonetic.features;

import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.structures.contracts.Streamable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Samantha Fiona McCabe
 * @version 0.1.0
 * @since 2016-03-26
 */
public interface FeatureArray<T>
		extends Comparable<FeatureArray<T>>, Streamable<T>, ModelBearer<T> {

	/**
	 * Returns the number of elements in this array.
	 *
	 * @return the number of elements in this array
	 */
	int size();

	/**
	 * Replaces the element at the specified position in this array with the
	 * specified element.
	 *
	 * @param index index of the element to replace
	 * @param value element to be stored at the specified position
	 * @throws IndexOutOfBoundsException - if the index is out of range
	 *  	{@code (index < 0 || index >= size())}
	 * @throws ClassCastException - if the class of the specified element
	 * 		prevents it from being added to this array
	 * @throws NullPointerException - if the specified element is null and
	 * 		this array does not permit null elements
	 */
	void set(int index, @Nullable T value);

	/**
	 * Returns the element at the specified position in this object.
	 *
	 * @param index index of the element to return
	 * @return the element at the specified position in this list; if there
	 * 		element, return null.
	 *
	 * @throws IndexOutOfBoundsException - if the index is out of range (index
	 * 		< 0 || index >= size())
	 */
	@Nullable
	T get(int index);

	/**
	 * Determines if another feature array is consistent with this one. Two
	 * feature arrays are consistent with each other if all corresponding
	 * features are equal or one is undefined.
	 *
	 * @param array another feature array to compare to this one
	 * @return true if all features in either segment are equal or undefined
	 */
	boolean matches(@NotNull FeatureArray<T> array);

	/**
	 * Combines a feature array onto this one, applying all fully specified
	 * features from the other array onto this one. Changes are done in-place
	 *
	 * @param array an array from which to assign values; each present or
	 * 		non-null feature in this array will be written on to the corresponding
	 * 		value of this segment
	 * @return true iff changes were made; applying an array to itself, or an
	 * 		empty array (with no defined features) will make no changes and will
	 * 		return false
	 */
	boolean alter(@NotNull FeatureArray<T> array);

	/**
	 * Returns true if and only if this array contains the specified value.
	 *
	 * @param value the value to search for
	 * @return true if this array contains {@code value}, false otherwise
	 */
	boolean contains(@Nullable T value);
}
