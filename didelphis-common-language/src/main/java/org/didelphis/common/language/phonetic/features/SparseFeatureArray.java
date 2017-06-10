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

import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Created by samantha on 3/27/16.
 */
public final class SparseFeatureArray<N>
		implements FeatureArray<N> {
	
	private final FeatureModel<N> featureModel;
	private final Map<Integer, N> features;

	/**
	 * @param featureModel
	 */
	public SparseFeatureArray(FeatureModel<N> featureModel) {
		this.featureModel = featureModel;
		features = new HashMap<>();
	}

	/**
	 * @param list
	 * @param featureModel
	 */
	public SparseFeatureArray(List<N> list, FeatureModel<N> featureModel) {
		this(featureModel);
		for (int i = 0; i < list.size(); i++) {
			N value = list.get(i);
			if (value != null) {
				features.put(i, value);
			}
		}
	}

	/**
	 * @param array
	 */
	public SparseFeatureArray(SparseFeatureArray<N> array) {
		featureModel = array.getFeatureModel();
		features = new HashMap<>(array.features);
	}

	@Override
	public int size() {
		return featureModel.size();
	}

	@Override
	public void set(int index, N value) {
		indexCheck(index);
		features.put(index, value);
	}

	@Override
	public N get(int index) {
		indexCheck(index);
		return features.get(index);
	}

	@Override
	public boolean matches(FeatureArray<N> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}

		for (Entry<Integer, N> entry : features.entrySet()) {
			N x = entry.getValue();
			N y = array.get(entry.getKey());
			if ((featureModel.isDefined(y)) && !x.equals(y)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean alter(FeatureArray<N> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}

		boolean changed = false;
			for (int i = 0; i < size(); i++) {
				N v = array.get(i);
				if (featureModel.isDefined(v)) {
					changed |= true;
					features.put(i, v);
				}
			}
		return changed;
	}

	@Override
	public boolean contains(N value) {
		return features.containsValue(value);
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
	public Iterator<N> iterator() {
		final List<N> list = new ArrayList<>(size());
		Collections.fill(list, null);
		features.forEach(list::set);
		return list.iterator();
	}

	@Override
	public String toString() {
		return "SparseFeatureArray{" + features + '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof SparseFeatureArray)) { return false; }

		SparseFeatureArray<?> that = (SparseFeatureArray<?>) obj;

		return featureModel.equals(that.featureModel) &&
				features.equals(that.features);
	}

	@Override
	public FeatureModel<N> getFeatureModel() {
		return featureModel;
	}

	@Override
	public FeatureSpecification getSpecification() {
		return featureModel;
	}

	@Override
	public int hashCode() {
		return Objects.hash(features, features.size(), size());
	}

	private void indexCheck(int index) {
		int size = featureModel.size();
		if (index >= size) {
			throw new IndexOutOfBoundsException("Provided index " + index +
					" is larger than defined size "+ size + " for feature model " + featureModel);
		}
	}
}
