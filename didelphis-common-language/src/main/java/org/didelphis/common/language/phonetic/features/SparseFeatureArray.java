/******************************************************************************
 * Copyright (c) 2016. Samantha Fiona McCabe                                  *
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

package org.didelphis.common.language.phonetic.features;

import org.didelphis.common.language.phonetic.model.interfaces.FeatureModel;
import org.didelphis.common.language.phonetic.model.interfaces.FeatureSpecification;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 3/27/16.
 */
public final class SparseFeatureArray<N extends Number>
		implements FeatureArray<N> {
	
	private final FeatureModel<N> featureModel;
	private final Map<Integer, N> features;

	public SparseFeatureArray(FeatureModel<N> featureModel) {
		this.featureModel = featureModel;
		features = new HashMap<>();
	}

	public SparseFeatureArray(List<N> list, FeatureModel<N> featureModel) {
		this(featureModel);
		for (int i = 0; i < list.size(); i++) {
			features.put(i, list.get(i));
		}
	}

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

		for (Map.Entry<Integer, N> entry : features.entrySet()) {
			N a = entry.getValue();
			N b = array.get(entry.getKey());
			if ((b != null) && !a.equals(b)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void alter(FeatureArray<N> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}

		if (array instanceof SparseFeatureArray) {
			features.putAll(((SparseFeatureArray<N>) array).features);
		} else{
			for (int i = 0; i < size(); i++) {
				N t = array.get(i);
				if (t != null) {
					features.put(i, t);
				}
			}
		}
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
			int comparison;

			if (x == null && y == null) {
				comparison = 0;
			} else if (x == null) {
				comparison = -1;
			}else if (y == null) {
				comparison = 1;
			} else {
				if (x instanceof Comparable && y instanceof Comparable) {
					Comparable<Object> xC = (Comparable<Object>) x;
					Comparable<Object> yC = (Comparable<Object>) y;
					comparison = xC.compareTo(yC);
				} else {
					comparison = String.valueOf(x).compareTo(String.valueOf(y));
				}
			}
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
		return features.values().iterator();
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
	public int hashCode() {
		int result = features.hashCode();
		result = 31 * result + features.hashCode();
		return result;
	}

	@Override
	public FeatureModel<N> getFeatureModel() {
		return featureModel;
	}

	@Override
	public FeatureSpecification getSpecification() {
		return featureModel;
	}

	private void indexCheck(int index) {
		int size = featureModel.size();
		if (index >= size) {
			throw new IndexOutOfBoundsException("Provided index " + index +
					" is larger than defined size "+ size + " for feature model " + featureModel);
		}
	}
}
