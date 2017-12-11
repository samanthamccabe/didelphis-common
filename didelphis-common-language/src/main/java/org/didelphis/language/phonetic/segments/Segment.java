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

package org.didelphis.language.phonetic.segments;

import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.language.phonetic.features.FeatureArray;
import lombok.NonNull;

/**
 * Interface {@code Segment}
 *
 * @param <T> the type of feature used by the segment
 *
 * @author Samantha Fiona McCabe
 * @date 2017-02-15
 * @since 0.1.0
 */
public interface Segment<T> extends ModelBearer<T>, Comparable<Segment<T>> {

	/**
	 * Combines the two segments, applying all fully specified features from the
	 * other segment onto this one. Changes are done in-place
	 *
	 * @param segment a segment from which to assign values; each present or
	 * non-null feature in this segment will be written on to the corresponding
	 * value of this segment
	 *
	 * @return true iff changes were made; applying a segment to itself, or an
	 * empty segment (with no defined features) will make no changes and will
	 * return false
	 */
	boolean alter(@NonNull Segment<T> segment);

	/**
	 * Determines if a segment is consistent with this segment. Two segments are
	 * consistent with each other if all corresponding features are equal one is
	 * undefined; usually this means null, but for numerical values of parameter
	 * {@code <T>} this could mean {@code NaN}
	 *
	 * @param segment another segment to compare to this one
	 *
	 * @return true if all features in either segment are equal or undefined
	 */
	boolean matches(@NonNull Segment<T> segment);

	/**
	 * @return the symbol associated with this segment
	 */
	@NonNull
	String getSymbol();

	/**
	 * @return the feature object representing this segment
	 */
	@NonNull
	FeatureArray<T> getFeatures();
}
