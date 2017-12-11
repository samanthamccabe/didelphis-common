/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.language.phonetic.model;

import org.didelphis.language.parsing.ParseException;
import org.didelphis.language.phonetic.SpecificationBearer;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.features.FeatureType;
import lombok.NonNull;

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
 * @date 7/31/2016
 */
public interface FeatureModel<T> extends SpecificationBearer {

	/**
	 * Retrieve this model's value {@code Constraint}s
	 * @return a list of feature value constraints; should be immutable
	 */
	@NonNull
	List<Constraint<T>> getConstraints();

	/**
	 * Parses a well-formed feature {@code String} into the corresponding array
	 * @param string the bracketed feature string definition to parse
	 * @return a parsed {@code FeatureArray}
	 *
	 * @throws ParseException if the
	 *
	 */
	@NonNull
	FeatureArray<T> parseFeatureString(@NonNull String string);

	/**
	 *
	 * @return
	 */
	@NonNull
	FeatureType<T> getFeatureType();
}
