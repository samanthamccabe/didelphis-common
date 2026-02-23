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

package org.didelphis.language.phonetic.features;

import lombok.NonNull;

import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.structures.contracts.Streamable;

import org.jetbrains.annotations.Nullable;

/**
 * @version 0.1.0
 * @since 2016-03-26
 */
public interface FeatureArray
		extends Comparable<FeatureArray>, Streamable<Integer>, ModelBearer {

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
	 *      prevents it from being added to this array
	 * @throws NullPointerException - if the specified element is null and
	 *      this array does not permit null elements
	 */
	void set(int index, @Nullable Integer value);

	/**
	 * Returns the element at the specified position in this object.
	 *
	 * @param index index of the element to return
	 * @return the element at the specified position in this list; if there
	 *      element, return null.
	 *
	 * @throws IndexOutOfBoundsException - if the index is out of range (index
	 *      < 0 || index >= size())
	 */
	@Nullable
	Integer get(int index);

	/**
	 * Determines if another feature array is consistent with this one. Two
	 * feature arrays are consistent with each other if all corresponding
	 * features are equal or one is undefined.
	 *
	 * @param array another feature array to compare to this one
	 * @return true if all features in either segment are equal or undefined
	 */
	boolean matches(@NonNull FeatureArray array);

	/**
	 * Combines a feature array onto this one, applying all fully specified
	 * features from the other array onto this one. Changes are done in-place
	 *
	 * @param array an array from which to assign values; each present or
	 *      non-null feature in this array will be written on to the corresponding
	 *      value of this segment
	 * @return true iff changes were made; applying an array to itself, or an
	 *      empty array (with no defined features) will make no changes and will
	 *      return false
	 */
	boolean alter(@NonNull FeatureArray array);

	/**
	 * Returns true if and only if this array contains the specified value.
	 *
	 * @param value the value to search for
	 * @return true if this array contains {@code value}, false otherwise
	 */
	boolean contains(@Nullable Integer value);
}
