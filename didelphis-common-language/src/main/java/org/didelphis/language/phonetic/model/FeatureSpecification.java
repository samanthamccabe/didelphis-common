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

package org.didelphis.language.phonetic.model;

import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * The {@code FeatureSpecification} represents a set of features and their types
 * <p>
 * This interface underpins {@link FeatureModel} and {@link FeatureMapping}
 *
 * @since 0.1.0
 */
public interface FeatureSpecification {

	/**
	 * Returns the number of features in the specification
	 *
	 * @return the number of features in the specification
	 */
	int size();

	/**
	 * Returns a map from feature name and abbreviation to it's index
	 *
	 * @return a map from feature name and abbreviation to it's index
	 */
	@NonNull Map<String, Integer> getFeatureIndices();

	/**
	 * Returns the index of the provided feature name or code.
	 *
	 * @param featureName the feature name or code whose index is to be found
	 *
	 * @return the index of the provided feature name or -1 if
	 *        {@code featureName} is not found in this specification
	 */
	int getIndex(@NonNull String featureName);

	/**
	 * Returns a list of the feature names defined in the specification
	 *
	 * @return a list of the feature names defined in the specification
	 */
	@NonNull List<String> getFeatureNames();

}
