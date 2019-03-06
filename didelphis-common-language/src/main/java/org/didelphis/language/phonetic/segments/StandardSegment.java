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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.didelphis.language.phonetic.segments;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;

/**
 * Class {@code StandardSegment}
 * 
 * @param <T> the type of feature data used by the segment's model
 * 
 * @since 0.1.0
 */
@EqualsAndHashCode
public class StandardSegment<T> implements Segment<T> {

	private final String symbol;
	private final FeatureArray<T> features;

	/**
	 * Copy constructor -
	 *
	 * @param segment the {@code Segment} to be copied
	 */
	public StandardSegment(@NonNull Segment<T> segment) {
		symbol = segment.getSymbol();
		features = segment.getFeatures();
	}

	/**
	 * Standard constructor -
	 *
	 * @param symbol the phonetic symbol representing this segment
	 * @param featureArray the feature array representing this segment
	 */
	public StandardSegment(String symbol, FeatureArray<T> featureArray) {
		this.symbol = symbol;
		features = featureArray;
	}

	/**
	 * Combines the two segments, applying all fully specified features from the
	 * other segment onto this one
	 *
	 * @param segment an underspecified segment from which to take changes
	 * @return a new segment based on this one with modifications from the other
	 */
	@Override
	public boolean alter(@NonNull Segment<T> segment) {
		return features.alter(segment.getFeatures());
	}

	@NonNull
	@Override
	public String getSymbol() {
		return symbol;
	}

	@NonNull
	@Override
	public FeatureArray<T> getFeatures() {
		return features;
	}

	@Override
	public boolean isDefinedInModel() {
		return true;
	}

	@NonNull
	@Override
	public FeatureModel<T> getFeatureModel() {
		return features.getFeatureModel();
	}

	@NonNull
	@Override
	public FeatureSpecification getSpecification() {
		return features.getSpecification();
	}

	@Override
	public String toString() {
		return symbol;
	}
}
