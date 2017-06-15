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

import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.language.phonetic.model.FeatureSpecification;

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
public final class SparseFeatureArray<T>
		implements FeatureArray<T> {
	
	private final FeatureModel<T> featureModel;
	private final Map<Integer, T> features;

	/**
	 * @param featureModel
	 */
	public SparseFeatureArray(FeatureModel<T> featureModel) {
		this.featureModel = featureModel;
		features = new HashMap<>();
	}

	/**
	 * @param list
	 * @param featureModel
	 */
	public SparseFeatureArray(List<T> list, FeatureModel<T> featureModel) {
		this(featureModel);
		for (int i = 0; i < list.size(); i++) {
			T value = list.get(i);
			if (value != null) {
				features.put(i, value);
			}
		}
	}

	/**
	 * @param array
	 */
	public SparseFeatureArray(SparseFeatureArray<T> array) {
		featureModel = array.getFeatureModel();
		features = new HashMap<>(array.features);
	}

	@Override
	public int size() {
		return featureModel.size();
	}

	@Override
	public void set(int index, T value) {
		indexCheck(index);
		features.put(index, value);
	}

	@Override
	public T get(int index) {
		indexCheck(index);
		return features.get(index);
	}

	@Override
	public boolean matches(FeatureArray<T> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}
		FeatureType<T> featureType = featureModel.getFeatureType();
		for (Entry<Integer, T> entry : features.entrySet()) {
			T x = entry.getValue();
			T y = array.get(entry.getKey());
			if ((featureType.isDefined(y)) && !x.equals(y)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean alter(FeatureArray<T> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}
		FeatureType<T> featureType = featureModel.getFeatureType();

		boolean changed = false;
			for (int i = 0; i < size(); i++) {
				T v = array.get(i);
				if (featureType.isDefined(v)) {
					changed |= true;
					features.put(i, v);
				}
			}
		return changed;
	}

	@Override
	public boolean contains(T value) {
		return features.containsValue(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(FeatureArray<T> o) {
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
	public Iterator<T> iterator() {
		final List<T> list = new ArrayList<>(size());
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
	public FeatureModel<T> getFeatureModel() {
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
