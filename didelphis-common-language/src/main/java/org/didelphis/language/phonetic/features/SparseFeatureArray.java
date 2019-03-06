/******************************************************************************
 * General components for language modeling and analysis                      *
 *                                                                            *
 * Copyright (C) 2014-2019 Samantha F McCabe                                  *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package org.didelphis.language.phonetic.features;

import lombok.NonNull;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.didelphis.utilities.Templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Class {@code SparseFeatureArray}
 *
 * @date 2016-03-27
 * @since 0.1.0
 */
public final class SparseFeatureArray<T> extends AbstractFeatureArray<T> {

	private final Map<Integer, T> features;

	/**
	 * @param featureModel
	 */
	public SparseFeatureArray(@NonNull FeatureModel<T> featureModel) {
		super(featureModel);
		features = new TreeMap<>();
	}

	/**
	 * @param list
	 * @param featureModel
	 */
	public SparseFeatureArray(
			@NonNull List<T> list, @NonNull FeatureModel<T> featureModel
	) {
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
	public SparseFeatureArray(@NonNull FeatureArray<T> array) {
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
	public SparseFeatureArray(@NonNull SparseFeatureArray<T> array) {
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
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof SparseFeatureArray)) return false;
		SparseFeatureArray<?> array = (SparseFeatureArray<?>) obj;
		return super.equals(obj) && features.equals(array.features);
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		code *= features.entrySet().stream()
				.mapToInt(entry -> 
						entry.getValue().hashCode() ^ (entry.getKey() >> 1))
				.reduce(1, (k, v) -> k * v);
		return code;
	}
	
	@Override
	public boolean matches(@NonNull FeatureArray<T> array) {
		sizeCheck(array);
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
	public boolean alter(@NonNull FeatureArray<T> array) {
		sizeCheck(array);

		FeatureType<T> featureType = getFeatureModel().getFeatureType();

		boolean changed = false;
		for (int i = 0; i < size(); i++) {
			T v = array.get(i);
			if (featureType.isDefined(v)) {
				changed = true;
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
		List<T> list = new ArrayList<>(size());
		Collections.fill(list, null);
		features.forEach(list::set);
		return list.iterator();
	}

	@Override
	public String toString() {
		return features.entrySet()
				.stream()
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining(";", "{", "}"));
	}

	private void indexCheck(int index) {
		int size = getSpecification().size();
		if (index >= size) {
			String message = Templates.create()
					.add("Provided index {} is larger than the defined size {}",
							"of the feature model.")
					.with(index, size)
					.build();
			throw new IndexOutOfBoundsException(message);
		}
	}
}
