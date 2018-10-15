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
 * @author Samantha Fiona McCabe
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
