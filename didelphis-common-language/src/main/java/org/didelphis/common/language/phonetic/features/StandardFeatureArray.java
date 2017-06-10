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

package org.didelphis.common.language.phonetic.features;

import org.didelphis.common.language.phonetic.model.Constraint;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 3/26/2016
 */
public final class StandardFeatureArray<N>
		implements FeatureArray<N> {
	
	private final FeatureModel<N> featureModel;
	private final List<N> features;

	/**
	 * @param value
	 * @param featureModel
	 */
	public StandardFeatureArray(N value, FeatureModel<N> featureModel) {
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
	public StandardFeatureArray(List<N> list, FeatureModel<N> featureModel) {
		this.featureModel = featureModel;
		features = new ArrayList<>(list);
	}

	/**
	 * @param array
	 */
	public StandardFeatureArray(StandardFeatureArray<N> array) {
		featureModel = array.getFeatureModel();
		features = new ArrayList<>(array.features);
	}

	/**
	 * @param array
	 */
	public StandardFeatureArray(FeatureArray<N> array) {
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
	public void set(int index, N value) {
		features.set(index, value);
		applyConstraints(index);
	}

	@Override
	public N get(int index) {
		return features.get(index);
	}

	@Override
	public boolean matches(FeatureArray<N> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}

		for (int i = 0; i < size(); i++) {
			N x = get(i);
			N y = array.get(i);
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

	private boolean matches(N x, N y) {
		return !(featureModel.isDefined(x) && featureModel.isDefined(y)) ||
				         x.equals(y);
	}

	@Override
	public boolean alter(FeatureArray<N> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}

		final Collection<Integer> alteredIndices = new HashSet<>();
		for (int i = 0; i < features.size(); i++) {
			N v = array.get(i);
			if (featureModel.isDefined(v)) {
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
		for (Constraint<N> constraint : featureModel.getConstraints()) {
			if (constraint.getSource().get(index) != null
			    && matches(constraint.getSource())) {
				alter(constraint.getTarget());
			}
		}
	}
	
	@Override
	public boolean contains(N value) {
		return features.contains(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(FeatureArray<N> o) {
		if (size() != o.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}

		for (int i = 0; i < size(); i++) {
			N x = get(i);
			N y = o.get(i);
			int comparison = featureModel.compare(x,y);
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
		if (!(obj instanceof StandardFeatureArray)) { return false; }

		StandardFeatureArray<?> that = (StandardFeatureArray<?>) obj;

		return features.equals(that.features);
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
	public Iterator<N> iterator() {
		return features.iterator();
	}

	@Override
	public FeatureModel<N> getFeatureModel() {
		return featureModel;
	}

}
