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

package org.didelphis.language.phonetic.segments;

import lombok.NonNull;

import org.didelphis.language.phonetic.ModelBearer;
import org.didelphis.language.phonetic.features.FeatureArray;

/**
 * Interface {@code Segment}
 *
 * @param <T> the type of feature data used by the segment's model
 *
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
	 * consistent with each other if all corresponding features are equal OR if
	 * one is NaN
	 *
	 * @param segment another segment to compare to this one
	 * @return true if all specified (non NaN) features in either segment are
	 *      equal
	 */
	default boolean matches(@NonNull Segment<T> segment) {
		if (getFeatureModel().getSpecification().size() == 0 &&
				segment.getFeatureModel().getSpecification().size() == 0) {
			return getSymbol().equals(segment.getSymbol());
		}
		if (isDefinedInModel() && segment.isDefinedInModel()) {
			return getFeatures().matches(segment.getFeatures());
		} else if (!isDefinedInModel() && !segment.isDefinedInModel()) {
			return getSymbol().equals(segment.getSymbol());
		} else {
			return false;
		}
	}
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

	/**
	 * Indicates whether a segment is properly defined it's {@link
	 * org.didelphis.language.phonetic.model.FeatureModel} of origin and has
	 * an associated feature structure, or if it is defined only in terms of its
	 * symbol.
	 * @return
	 */
	boolean isDefinedInModel();

	@Override
	default int compareTo(@NonNull Segment<T> o) {
		if (equals(o)) {
			return 0;
		} else {
			int value = getFeatures().compareTo(o.getFeatures());
			if (value == 0) {
				// If we get here, there is either no features, or feature
				// arrays are equal so just compare the symbols
				int difference = getSymbol().compareTo(o.getSymbol());
				// however, calling compareTo on Strings is not like an object
				// which implements Comparable; returned values can be larger
				// than |1|
				if (difference > 0) {
					return 1;
				} else if (difference < 0) {
					return -1;
				} else {
					return 0;
				}
			}
			return value;
		}
	}
}
