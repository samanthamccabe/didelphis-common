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
import org.didelphis.language.phonetic.model.FeatureModelLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Interface {@code FeatureType}
 *
 * @param <T> the value type of the feature 
 * 
 * Defines the interactions between a feature type and the data type {@code <T>}
 * used to store that type, such as between {@link BinaryFeature} and
 * {@link Boolean}.
 *
 * Within linguistics, the representation of binary features with {@code +} and
 * {@code -} is well established, and {@link BinaryFeature} specifies how these
 * should be interpreted when parsing {@link String}s. Similarly, it specifies
 * what values should be considered undefined, such as {@code null}, or other
 * values.
 *
 * @since 0.1.0
 *
 * 2017-06-11
 */
public interface FeatureType<T> {

	/**
	 * 
	 * @return
	 */
	FeatureModelLoader<T> emptyLoader();
	
	/**
	 * Parses the {@code String} argument as a value of type {@code <T>}. This 
	 * behaves similarly to {@link Integer#parseInt(String)}, {@link 
	 * Double#parseDouble(String)} etc. but depends on the specific 
	 * implementation of the model. A specific implementation may simply 
	 * delegate to those methods, or could augment their behavior in various 
	 * ways.
	 *
	 * @param string the {@code String} containing the value to be parsed
	 *
	 * @return the value represented by the string argument
	 *
	 * @throws NumberFormatException if the {@code String} does not contain a
	 * 		parseable value;.
	 */
	@NonNull
	T parseValue(@NonNull String string);

	@NonNull
	Collection<T> listUndefined();

	/**
	 * Checks if the given value is defined or undefined according to the this
	 * feature model.
	 * @param value the value to be checked
	 * @return true iff the value is defined
	 */
	default boolean isDefined(@Nullable T value) {
		return !(value == null || listUndefined().contains(value));
	}

	/**
	 * Compares one object with the another object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * When {@code N} implements {@link Comparable} this method should just
	 * return {@code v1.compareTo(v2)}
	 *
	 * @param v1 the first parameter in the comparison
	 * @param v2 the second parameter in the comparison
	 * @return -1, 0, or 1
	 * @see Comparable;
	 */
	int compare(@Nullable T v1,@Nullable  T v2);

	/**
	 * Determines the absolute value of the difference between the inputs.
	 * @param v1 the first parameter
	 * @param v2 the second parameter
	 * @return the absolute value of the difference as a {@code double}
	 */
	double difference(@Nullable T v1,@Nullable T v2);

	/**
	 * Returns the {@code int} value of this given value;
	 * @param value the value to be converted
	 * @return  the {@code int} represented by a given value
	 */
	int intValue(@Nullable T value);

	/**
	 * Returns the {@code double} value of this given value;
	 * @param value the value to be converted
	 * @return the {@code double} represented by a given value
	 */
	double doubleValue(@Nullable T value);

	/**
	 * A convenience function for determining the total difference between
	 * arrays 
	 * @param index 
	 * @param left
	 * @param right
	 * @return the difference between two feature arrays at the specified index
	 */
	default double difference(
			int index,
			@NonNull FeatureArray<T> left,
			@NonNull FeatureArray<T> right
	) {
		return difference(left.get(index), right.get(index));
	}

	/**
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	default double difference(
			@NonNull FeatureArray<T> left,
			@NonNull FeatureArray<T> right
	) {
		assert left.size() == right.size() : "Feature arrays not of same size.";
		double sum = 0.0;
		for (int i = 0; i < left.size(); i++) {
			double difference = difference(i, left, right);
			sum += difference;
		}
		return sum;
	}
}
