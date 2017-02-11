/*******************************************************************************
 * Copyright (c) 2015. Samantha Fiona McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.didelphis.common.language.phonetic;

import com.sun.istack.internal.NotNull;
import org.didelphis.common.language.phonetic.features.FeatureArray;
import org.didelphis.common.language.phonetic.features.SparseFeatureArray;
import org.didelphis.common.language.phonetic.features.StandardFeatureArray;
import org.didelphis.common.language.phonetic.model.Constraint;
import org.didelphis.common.language.phonetic.model.FeatureSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Samantha Fiona Morrigan McCabe
 */
public class Segment implements SpecificationBearer, Comparable<Segment> {

	public static final Segment EMPTY_SEGMENT = new Segment("∅");

	private final FeatureSpecification specification;
	private final String       symbol;
	private final FeatureArray<Double> features;

	// Copy-constructor
	public Segment(Segment segment) {
		symbol = segment.symbol;
		specification = segment.specification;
		features = segment.features;
	}

	public Segment(String s, FeatureArray<Double> featureArray,
	               FeatureSpecification modelParam) {
		symbol = s;
		specification = modelParam;
		features = featureArray;
	}

	// Used to create the empty segment
	private Segment(String string) {
		symbol = string;
		specification = FeatureSpecification.EMPTY;
		features = new StandardFeatureArray<>(FeatureSpecification.UNDEFINED_VALUE, FeatureSpecification.EMPTY);
	}

	/**
	 * Combines the two segments, applying all fully specified features from
	 * the other segment onto this one
	 * @param other an underspecified segment from which to take changes
	 * @return a new segment based on this one with modifications from the other
	 */
	public Segment alter(Segment other) {
		validateModelOrFail(other);

		Collection<Integer> alteredIndices = new ArrayList<>();
		FeatureArray<Double> newFeatures = new StandardFeatureArray<>(features);
		for (int j = 0; j < newFeatures.size(); j++) {
			Double value = other.getFeatureValue(j);
			if (value != null) {
				newFeatures.set(j, value);
				alteredIndices.add(j);
			}
		}

		// For each altered index, check if the constraints apply 
		for (int index : alteredIndices) {
			for (Constraint constraint : specification.getConstraints()) {
				applyConstraint(index, newFeatures, constraint);
			}
		}
		
		
		
		return new Segment(symbol, newFeatures, specification);
	}

	/**
	 * Determines if a segment is consistent with this segment. Two segments are
	 * consistent with each other if all corresponding features are equal OR if
	 * one is NaN
	 *
	 * @param other another segment to compare to this one
	 * @return true if all specified (non NaN) features in either segment are equal
	 */
	public boolean matches(Segment other) {
		validateModelOrFail(other);

		int size = features.size();
		if (isUndefined() && other.isUndefined()) {
			return symbol.equals(other.symbol);
		} else if (size > 0) {
			return features.matches(other.getFeatures());
		} else {
			return equals(other);
		}
	}

	@Override
	public FeatureSpecification getSpecification() {
		return specification;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Segment)) {
			return false;
		}
		Segment segment = (Segment) o;
		return Objects.equals(specification, segment.specification)
			         && Objects.equals(symbol, segment.symbol)
			         && Objects.equals(features, segment.features);
	}

	@Override
	public int hashCode() {
		return Objects.hash(specification, symbol, features);
	}

	@Override
	public String toString() {
		return symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public FeatureArray<Double> getFeatures() {
		return features;
	}

	public Double getFeatureValue(int i) {
		return features.get(i);
	}

	public String toStringLong() {
		StringBuilder sb = new StringBuilder(symbol + '\t');
		for (Double feature : features) {
			if (feature.equals(Double.NaN)) {
				sb.append(" ***");
			} else {
				if (feature < 0.0) {
					sb.append(feature);
				} else {
					sb.append(' ');
					sb.append(feature);
				}
			}
			sb.append(' ');
		}
		return sb.toString();
	}

	public void setFeatureValue(int index, double value) {
		features.set(index, value);
		for (Constraint constraint : specification.getConstraints()) {
			applyConstraint(index, features, constraint);
		}
	}

	private static void applyConstraint(
			int index,
			FeatureArray<Double> features,
			Constraint constraint) {
		
		FeatureArray<Double> source = constraint.getSource();
		if (source.get(index) != null) {
			if (source.matches(features)) {
				features.alter(constraint.getTarget());
			}
		}
	}

	@Deprecated
	public boolean isUndefined() {
		return false;
	}

	@Deprecated
	public boolean isUnderspecified() {
		return features.contains(null) || features instanceof SparseFeatureArray;
	}

	@Override
	public int compareTo(@NotNull Segment o) {
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

	private void validateModelOrFail(SpecificationBearer that) {
		FeatureSpecification otherModel = that.getSpecification();
		if (!specification.equals(otherModel)) {
			throw new RuntimeException(
				"Attempting to inter-operate " + that.getClass() + " with an incompatible featureModel!\n" +
					'\t' + this + '\t' + specification.getFeatureNames() + '\n' +
					'\t' + that + '\t' + otherModel.getFeatureNames()
			);
		}
	}
}
