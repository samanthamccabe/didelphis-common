/******************************************************************************
 * Copyright (c) 2017. Samantha Fiona McCabe (Didelphis.org)                  *
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

package org.didelphis.language.phonetic.features;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.didelphis.language.phonetic.model.Constraint;
import org.didelphis.language.phonetic.model.FeatureModel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class {@code StandardFeatureArray}
 *
 * @since 0.1.0
 */
@EqualsAndHashCode(callSuper = true)
public final class StandardFeatureArray<T> extends AbstractFeatureArray<T> {

	private final List<T> features;

	/**
	 * @param value
	 * @param featureModel
	 */
	public StandardFeatureArray(
			@Nullable T value, @NonNull FeatureModel<T> featureModel
	) {
		super(featureModel);
		int size = getSpecification().size();
		features = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			features.add(value);
		}
	}

	/**
	 * @param list
	 * @param featureModel
	 */
	public StandardFeatureArray(
			@NonNull List<T> list, @NonNull FeatureModel<T> featureModel
	) {
		super(featureModel);
		features = new ArrayList<>(list);
	}

	/**
	 * @param array
	 */
	public StandardFeatureArray(@NonNull StandardFeatureArray<T> array) {
		super(array.getFeatureModel());
		features = new ArrayList<>(array.features);
	}

	/**
	 * @param array
	 */
	public StandardFeatureArray(@NonNull FeatureArray<T> array) {
		super(array.getFeatureModel());
		features = new ArrayList<>(size());
		for (int i = 0; i < size(); i++) {
			features.add(array.get(i));
		}
	}

	@Override
	public void set(int index, @Nullable T value) {
		features.set(index, value);
		applyConstraints(index);
	}

	@Override
	public @Nullable T get(int index) {
		return features.get(index);
	}

	@Override
	public boolean matches(@NonNull FeatureArray<T> array) {
		sizeCheck(array);

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
	public boolean alter(@NonNull FeatureArray<T> array) {
		sizeCheck(array);

		FeatureType<T> featureType = getFeatureModel().getFeatureType();
		Collection<Integer> alteredIndices = new HashSet<>();
		for (int i = 0; i < features.size(); i++) {
			T v = array.get(i);
			if (featureType.isDefined(v) && !Objects.equals(get(i), v)) {
				alteredIndices.add(i);
				features.set(i, v);
			}
		}
		for (int index : alteredIndices) {
			applyConstraints(index);
		}
		return !alteredIndices.isEmpty();
	}

	@Override
	public boolean contains(@Nullable T value) {
		return features.contains(value);
	}

	@Override
	public String toString() {
		return features.stream()
				.map(Objects::toString)
				.collect(Collectors.joining(";", "[[", "]]"));
	}

	@Override
	public Iterator<T> iterator() {
		return features.iterator();
	}

	private boolean matches(@Nullable T x, @Nullable T y) {
		FeatureType<T> featureType = getFeatureModel().getFeatureType();
		return !(featureType.isDefined(x) && featureType.isDefined(y))
				|| Objects.equals(x, y);
	}

	private void applyConstraints(int index) {
		for (Constraint<T> constraint : getFeatureModel().getConstraints()) {
			if (constraint.getSource().get(index) != null
					&& matches(constraint.getSource())) {
				alter(constraint.getTarget());
			}
		}
	}
}
