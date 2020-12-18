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
@EqualsAndHashCode (callSuper = true)
public final class StandardFeatureArray extends AbstractFeatureArray {

	private final List<Integer> features;

	/**
	 * @param value
	 * @param featureModel
	 */
	public StandardFeatureArray(
			@Nullable Integer value, @NonNull FeatureModel featureModel
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
			@NonNull List<Integer> list, @NonNull FeatureModel featureModel
	) {
		super(featureModel);
		features = new ArrayList<>(list);
	}

	/**
	 * @param array
	 */
	public StandardFeatureArray(@NonNull StandardFeatureArray array) {
		super(array.getFeatureModel());
		features = new ArrayList<>(array.features);
	}

	/**
	 * @param array
	 */
	public StandardFeatureArray(@NonNull FeatureArray array) {
		super(array.getFeatureModel());
		features = new ArrayList<>(size());
		for (int i = 0; i < size(); i++) {
			features.add(array.get(i));
		}
	}

	@Override
	public void set(int index, @Nullable Integer value) {
		features.set(index, value);
		applyConstraints(index);
	}

	@Override
	public @Nullable Integer get(int index) {
		return features.get(index);
	}

	@Override
	public boolean matches(@NonNull FeatureArray array) {
		sizeCheck(array);

		for (int i = 0; i < size(); i++) {
			Integer x = get(i);
			Integer y = array.get(i);
			if (!matches(x, y)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean alter(@NonNull FeatureArray array) {
		sizeCheck(array);

		FeatureType featureType = getFeatureModel().getFeatureType();
		Collection<Integer> alteredIndices = new HashSet<>();
		for (int i = 0; i < features.size(); i++) {
			Integer v = array.get(i);
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
	public boolean contains(@Nullable Integer value) {
		return features.contains(value);
	}

	@Override
	public String toString() {
		return features.stream()
				.map(Objects::toString)
				.collect(Collectors.joining(";", "[[", "]]"));
	}

	@Override
	public Iterator<Integer> iterator() {
		return features.iterator();
	}

	private boolean matches(@Nullable Integer x, @Nullable Integer y) {
		FeatureType featureType = getFeatureModel().getFeatureType();
		return !(featureType.isDefined(x) && featureType.isDefined(y)) ||
				Objects.equals(x, y);
	}

	private void applyConstraints(int index) {
		for (Constraint constraint : getFeatureModel().getConstraints()) {
			FeatureArray source = constraint.getSource();
			if (source.get(index) != null && matches(source)) {
				alter(constraint.getTarget());
			}
		}
	}
}
