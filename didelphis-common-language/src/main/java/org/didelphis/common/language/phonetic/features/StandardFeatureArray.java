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

import org.didelphis.common.language.phonetic.model.FeatureSpecification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 3/26/2016
 */
public final class StandardFeatureArray<T extends Number & Comparable<T>>
		implements FeatureArray<T> {
	
	private final FeatureSpecification specification;
	private final List<T> features;

	public StandardFeatureArray(T value, FeatureSpecification specification) {
		this.specification = specification;
		int size = specification.size();
		features = new ArrayList<T>(size);
		for (int i = 0; i < size; i++) {
			features.add(value);
		}
	}

	public StandardFeatureArray(List<T> list, FeatureSpecification specification) {
		this.specification = specification;
		features = new ArrayList<T>(list);
	}
	
	public StandardFeatureArray(StandardFeatureArray<T> array) {
		specification = array.getSpecification();
		features = new ArrayList<T>(array.features);
	}

	public StandardFeatureArray(FeatureArray<T> array) {
		specification = array.getSpecification();
		int size = specification.size();
		features = new ArrayList<T>(size);
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
			T a = get(i);
			T b = array.get(i);
			boolean matches =
					a == null ||
					b == null ||
					a.equals(b);
			if (!matches) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void alter(FeatureArray<T> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}

		for (int i = 0; i < features.size(); i++) {
			T t = array.get(i);
			if (t != null) {
				features.set(i, t);
			}
		}
	}

	@Override
	public boolean contains(T value) {
		return features.contains(value);
	}

	@Override
	public int compareTo(FeatureArray<T> o) {
		if (size() != o.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}

		for (int i = 0; i < size(); i++) {
			T a = get(i);
			T b = o.get(i);
			int comparison;

			if (a == null && b == null) {
				comparison = 0;
			} else if (a == null) {
				comparison = -1;
			}else if (b == null) {
				comparison = 1;
			} else {
				comparison = a.compareTo(b);
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
	public Iterator<T> iterator() {
		return features.iterator();
	}

	@Override
	public FeatureSpecification getSpecification() {
		return specification;
	}
}
