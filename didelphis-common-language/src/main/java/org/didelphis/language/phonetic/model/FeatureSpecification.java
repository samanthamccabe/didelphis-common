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

import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * The {@code FeatureSpecification} represents a set of features and their types
 *
 * This interface underpins {@link FeatureModel} and {@link FeatureMapping}
 *
 * @since 0.1.0
 *
 * @date 2017-02-16
 */
public interface FeatureSpecification {

	/**
	 * Returns the number of features in the specification
	 * @return the number of features in the specification
	 */
	int size();

	/**
	 * Returns a map from feature name and abbreviation to it's index
	 * @return a map from feature name and abbreviation to it's index
	 */
	@NonNull
	Map<String, Integer> getFeatureIndices();

	/**
	 * Returns the index of the provided feature name or code.
	 * @param featureName the feature name or code whose index is to be found
	 * @return the index of the provided feature name or -1 if
	 *      {@code featureName} is not found in this specification
	 */
	int getIndex(@NonNull String featureName);

	/**
	 * Returns a list of the feature names defined in the specification
	 * @return a list of the feature names defined in the specification
	 */
	@NonNull
	List<String> getFeatureNames();

}
