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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public final class SparseFeatureArray<T> extends AbstractFeatureArray<T> {
	
	private final Map<Integer, T> features;

	/**
	 * @param featureModel
	 */
	public SparseFeatureArray(FeatureModel<T> featureModel) {
super(featureModel);
features = new HashMap<>();
	}

	/**
	 * @param list
	 * @param featureModel
	 */
	public SparseFeatureArray(@NotNull List<T> list, FeatureModel<T> featureModel) {
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
	public SparseFeatureArray(@NotNull FeatureArray<T> array) {
		super(array.getFeatureModel());
		features = new HashMap<>();
		FeatureType<T> type = array.getFeatureModel().getFeatureType();
		for (int i = 0; i < array.size(); i++) {
			T t = array.get(i);
			if (type.isDefined(t)) {
				features.put(i, t);
			}
		}
	}

	/**
	 * @param array
	 */
	public SparseFeatureArray(@NotNull SparseFeatureArray<T> array) {
		super(array.getFeatureModel());
		features = new HashMap<>(array.features);
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
	public boolean matches(@NotNull FeatureArray<T> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}
		FeatureType<T> featureType = getFeatureModel().getFeatureType();
		for (Entry<Integer, T> entry : features.entrySet()) {
			T x = entry.getValue();
			T y = array.get(entry.getKey());
			if ((featureType.isDefined(y)) && !Objects.equals(x, y)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean alter(@NotNull FeatureArray<T> array) {
		if (size() != array.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}
		FeatureType<T> featureType = getFeatureModel().getFeatureType();

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


	@Override
	public Iterator<T> iterator() {
		final List<T> list = new ArrayList<>(size());
		Collections.fill(list, null);
		features.forEach(list::set);
		return list.iterator();
	}

	@Override
	public int hashCode() {
		return Objects.hash(features, features.size(), size());
	}

	@Override
	public String toString() {
		return "SparseFeatureArray{" + features + '}';
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return super.equals(o);
	}

	private void indexCheck(int index) {
		int size = getSpecification().size();
		if (index >= size) {
			throw new IndexOutOfBoundsException("Provided index " + index +
					" is larger than defined size "+ size +
					" for feature model " + getFeatureModel());
		}
	}
}
