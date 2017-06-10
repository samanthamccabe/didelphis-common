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

package org.didelphis.common.language.phonetic.model.interfaces;

import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.Constraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface {@code FeatureModel} is a {@link FeatureSpecification} which
 * has value constraints and a type parameter. It also provides a specification
 * for what feature values are  "defined" or "undefined" and defines a natural
 * ordering of elements.
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 *
 * Date: 7/31/2016
 */
public interface FeatureModel<N> extends FeatureSpecification {

	/**
	 * Retrieve this model's value {@code Constraint}s
	 * @return a list of feature value constraints; should be immutable
	 */
	@NotNull
	List<Constraint<N>> getConstraints();

	/**
	 * Parses a well-formed feature {@code String} into the corresponding array
	 * @param string the bracketed feature string definition to parse
	 * @return a parsed {@code FeatureArray}
	 *
	 * @throws org.didelphis.common.language.exceptions.ParseException if the
	 *
	 */
	@NotNull
	FeatureArray<N> parseFeatureString(@NotNull String string);

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
	N parseValue(@NotNull String string);

	/**
	 * Checks if the given value is defined or undefined according to the this
	 * feature model.
	 * @param value the value to be checked
	 * @return true iff the value is defined
	 */
	boolean isDefined(@Nullable N value);

	/**
	 * Compares one object with the another object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * When {@code N} implements {@link Comparable} this method should just
	 * return {@code v1.compareTo(v2)}
	 *
	 * @param v1 the first parameter to in the comparison
	 * @param v2 the second parameter in the comparison
	 * @return -1, 0, or 1
	 * @see Comparable;
	 */
	int compare(N v1, N v2);
}
