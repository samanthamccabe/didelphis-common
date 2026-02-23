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
import java.util.stream.Collectors;

/**
 * Class {@code SparseFeatureArray}
 *
 * @since 0.1.0
 */
public final class SparseFeatureArray extends AbstractFeatureArray {

	private final Map<Integer, Integer> features;

	/**
	 * @param featureModel
	 */
	public SparseFeatureArray(@NonNull FeatureModel featureModel) {
		super(featureModel);
		features = new HashMap<>();
	}

	/**
	 * @param list
	 * @param featureModel
	 */
	public SparseFeatureArray(
			@NonNull List<Integer> list, @NonNull FeatureModel featureModel
	) {
		this(featureModel);
		for (int i = 0; i < list.size(); i++) {
			Integer value = list.get(i);
			if (value != null) {
				features.put(i, value);
			}
		}
	}

	/**
	 * @param array
	 */
	public SparseFeatureArray(@NonNull FeatureArray array) {
		super(array.getFeatureModel());
		features = new HashMap<>();
		FeatureType type = array.getFeatureModel().getFeatureType();
		for (int i = 0; i < array.size(); i++) {
			Integer Integer = array.get(i);
			if (type.isDefined(Integer)) {
				features.put(i, Integer);
			}
		}
	}

	/**
	 * @param array
	 */
	public SparseFeatureArray(@NonNull SparseFeatureArray array) {
		super(array.getFeatureModel());
		features = new HashMap<>(array.features);
	}

	@Override
	public void set(int index, Integer value) {
		indexCheck(index);
		features.put(index, value);
	}

	@Override
	public Integer get(int index) {
		indexCheck(index);
		return features.get(index);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof SparseFeatureArray)) return false;
		SparseFeatureArray array = (SparseFeatureArray) obj;
		return super.equals(obj) && features.equals(array.features);
	}

	@Override
	public int hashCode() {
		int code = super.hashCode();
		code *= features.entrySet()
				.stream()
				.mapToInt(tEntry -> 31 * tEntry.getKey() *
						Objects.hashCode(tEntry.getValue()))
				.reduce(1, (a, b) -> a * b);
		return code;
	}

	@Override
	public boolean matches(@NonNull FeatureArray array) {
		sizeCheck(array);
		FeatureType featureType = getFeatureModel().getFeatureType();
		for (Entry<Integer, Integer> entry : features.entrySet()) {
			Integer x = entry.getValue();
			Integer y = array.get(entry.getKey());
			if ((featureType.isDefined(y)) && !Objects.equals(x, y)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean alter(@NonNull FeatureArray array) {
		sizeCheck(array);

		FeatureType featureType = getFeatureModel().getFeatureType();

		boolean changed = false;
		for (int i = 0; i < size(); i++) {
			Integer v = array.get(i);
			if (featureType.isDefined(v)) {
				changed = true;
				features.put(i, v);
			}
		}
		return changed;
	}

	@Override
	public boolean contains(Integer value) {
		return features.containsValue(value);
	}


	@Override
	public Iterator<Integer> iterator() {
		List<Integer> list = new ArrayList<>(size());
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
		if (index >= size()) {
			String message = Templates.create().add(
					"Provided index {} is larger than the defined size {}",
					"of the feature model."
			).with(index, size()).build();
			throw new IndexOutOfBoundsException(message);
		}
	}
}
