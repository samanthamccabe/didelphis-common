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
import org.didelphis.common.language.phonetic.features.FeatureType;
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
public interface FeatureModel<T> extends FeatureSpecification {

	/**
	 * Retrieve this model's value {@code Constraint}s
	 * @return a list of feature value constraints; should be immutable
	 */
	@NotNull
	List<Constraint<T>> getConstraints();

	/**
	 * Parses a well-formed feature {@code String} into the corresponding array
	 * @param string the bracketed feature string definition to parse
	 * @return a parsed {@code FeatureArray}
	 *
	 * @throws org.didelphis.common.language.exceptions.ParseException if the
	 *
	 */
	@NotNull
	FeatureArray<T> parseFeatureString(@NotNull String string);

	/**
	 *
	 * @return
	 */
	@NotNull
	FeatureType<T> getFeatureType();
}
