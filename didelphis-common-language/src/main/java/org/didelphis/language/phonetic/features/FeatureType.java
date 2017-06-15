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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class {@code FeatureType}
 *
 * Defines the interactions between a feature type and the data type {@code T}
 * used to store that type, such as between {@link BinaryFeature} and
 * {@link Boolean}.
 *
 * Within linguistics, the representation of binary features with {@code +} and
 * {@code -} is well established, and {@link BinaryFeature} specifies how these
 * should be interpreted when parsing {@link String}s. Similarly, it specifies
 * what values should be considered undefined, such as {@code null}, or other
 * values.
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 *
 * Date: 2017-06-11
 */
public interface FeatureType<T> {

	/**
	 * Parses the {@code String} argument as a value of type {@code N}. This
	 * behaves similarly to {@link Integer#parseInt(String)},
	 * {@link Double#parseDouble(String)} etc. but depends on the specific
	 * implementation of the model. A specific implementation may simply
	 * delegate to those methods, or could augment their behavior in various
	 * ways.
	 * @param string the {@code String} containing the value to be parsed
	 * @return the value represented by the string argument
	 *
	 * @throws NumberFormatException if the {@code String} does not contain a
	 * parsable value;.
	 */
	@NotNull
	T parseValue(@NotNull String string);

	/**
	 * Checks if the given value is defined or undefined according to the this
	 * feature model.
	 * @param value the value to be checked
	 * @return true iff the value is defined
	 */
	boolean isDefined(@Nullable T value);

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
}
