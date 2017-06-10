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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.didelphis.common.language.phonetic.segments;

import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Samantha Fiona McCabe
 * @since 0.1.0 Date: 2017-03-16
 */
public class StandardSegment<N> implements Segment<N> {

	private final String symbol;
	private final FeatureModel<N> featureModel;
	private final FeatureArray<N> features;

	/**
	 * Copy constructor -
	 *
	 * @param segment the {@code Segment} to be copied
	 */
	public StandardSegment(StandardSegment<N> segment) {
		symbol = segment.symbol;
		featureModel = segment.featureModel;
		features = segment.features;
	}

	/**
	 * Standard constructor -
	 *
	 * @param symbol the phonetic symbol representing this segment
	 * @param featureArray the feature array representing this segment
	 * @param modelParam the model corresponding to the symbol and feature
	 * 		array
	 */
	public StandardSegment(String symbol, FeatureArray<N> featureArray,
			FeatureModel<N> modelParam) {
		this.symbol = symbol;
		featureModel = modelParam;
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
	public boolean alter(@NotNull Segment<N> segment) {
		return features.alter(segment.getFeatures());
	}

	/**
	 * Determines if a segment is consistent with this segment. Two segments are
	 * consistent with each other if all corresponding features are equal OR if
	 * one is NaN
	 *
	 * @param segment another segment to compare to this one
	 * @return true if all specified (non NaN) features in either segment are
	 * 		equal
	 */
	@Override
	public boolean matches(@NotNull Segment<N> segment) {
		if (features.size() == 0 && segment.getFeatures().size() == 0) {
			return symbol.equals(segment.getSymbol());
		}
		return features.matches(segment.getFeatures());
	}

	@NotNull
	@Override
	public String getSymbol() {
		return symbol;
	}

	@NotNull
	@Override
	public FeatureArray<N> getFeatures() {
		return features;
	}

	@Override
	public FeatureModel<N> getFeatureModel() {
		return featureModel;
	}

	@Override
	public int hashCode() {
		return Objects.hash(featureModel, symbol, features);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StandardSegment)) {
			return false;
		}
		StandardSegment<?> segment = (StandardSegment<?>) o;
		return Objects.equals(featureModel, segment.featureModel) &&
				Objects.equals(symbol, segment.symbol) &&
				Objects.equals(features, segment.features);
	}

	@Override
	public String toString() {
		return symbol;
	}

	@Override
	public int compareTo(Segment<N> o) {
		if (equals(o)) {
			return 0;
		} else {
			int value = features.compareTo(o.getFeatures());
			if (value == 0) {
				// If we get here, there is either no features, or feature
				// arrays are equal so just compare the symbols
				return symbol.compareTo(o.getSymbol());
			}
			return value;
		}
	}

	@Override
	public FeatureSpecification getSpecification() {
		return featureModel;
	}

}
