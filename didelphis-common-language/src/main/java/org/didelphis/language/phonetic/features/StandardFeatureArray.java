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

package org.didelphis.language.phonetic.features;

import org.didelphis.language.phonetic.model.Constraint;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 3/26/2016
 */
public final class StandardFeatureArray<T>
		implements FeatureArray<T> {
	
	private final FeatureModel<T> featureModel;
	private final List<T> features;

	/**
	 * @param value
	 * @param featureModel
	 */
	public StandardFeatureArray(T value, FeatureModel<T> featureModel) {
		this.featureModel = featureModel;
		int size = featureModel.size();
		features = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			features.add(value);
		}
	}

	/**
	 *
	 * @param list
	 * @param featureModel
	 */
	public StandardFeatureArray(List<T> list, FeatureModel<T> featureModel) {
		this.featureModel = featureModel;
		features = new ArrayList<>(list);
	}

	/**
	 * @param array
	 */
	public StandardFeatureArray(StandardFeatureArray<T> array) {
		featureModel = array.getFeatureModel();
		features = new ArrayList<>(array.features);
	}

	/**
	 * @param array
	 */
	public StandardFeatureArray(FeatureArray<T> array) {
		featureModel = array.getFeatureModel();
		int size = featureModel.size();
		features = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			features.add(array.get(i));
		}
	}

	@Override
	public int size() {
		return features.size();
	}

	@Override
	public void set(int index, T value) {
		features.set(index, value);
		applyConstraints(index);
	}

	@Override
	public T get(int index) {
		return features.get(index);
	}

	@Override
	public boolean matches(FeatureArray<T> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}

		for (int i = 0; i < size(); i++) {
			T x = get(i);
			T y = array.get(i);
			if (!matches(x, y)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public FeatureSpecification getSpecification() {
		return featureModel;
	}

	private boolean matches(T x, T y) {
		FeatureType<T> featureType = featureModel.getFeatureType();
		return !(featureType.isDefined(x) && featureType.isDefined(y)) ||
				         x.equals(y);
	}

	@Override
	public boolean alter(FeatureArray<T> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}
		FeatureType<T> featureType = featureModel.getFeatureType();

		final Collection<Integer> alteredIndices = new HashSet<>();
		for (int i = 0; i < features.size(); i++) {
			T v = array.get(i);
			if (featureType.isDefined(v)) {
				alteredIndices.add(i);
				features.set(i, v);
			}
		}
		for (int index : alteredIndices) {
			applyConstraints(index);
		}
		return !alteredIndices.isEmpty();
	}

	private void applyConstraints(int index) {
		for (Constraint<T> constraint : featureModel.getConstraints()) {
			if (constraint.getSource().get(index) != null
			    && matches(constraint.getSource())) {
				alter(constraint.getTarget());
			}
		}
	}
	
	@Override
	public boolean contains(T value) {
		return features.contains(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(@NotNull FeatureArray<T> o) {
		if (size() != o.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}
		FeatureType<T> featureType = featureModel.getFeatureType();
		for (int i = 0; i < size(); i++) {
			T x = get(i);
			T y = o.get(i);
			int comparison = featureType.compare(x,y);
			if (comparison != 0) {
				return comparison;
			}
			// Else, do nothing; the loop will check the next value
		}
		// If we get to the end, then all values must be equal
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof FeatureArray)) { return false; }
		FeatureArray<?> that = (FeatureArray<?>) obj;
		if (!Objects.equals(featureModel, that.getFeatureModel())) return false;
		for (int i = 0; i < featureModel.size(); i++) {
			if (!Objects.equals(get(i), that.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return features.hashCode();
	}

	@Override
	public String toString() {
		return features.toString();
	}

	@Override
	public Iterator<T> iterator() {
		return features.iterator();
	}

	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

}
