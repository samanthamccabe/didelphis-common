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
import org.didelphis.language.phonetic.model.FeatureSpecification;
import org.didelphis.utilities.Templates;

import java.util.Objects;

/**
 * Class {@code AbstractFeatureArray}
 *
 * @since 0.1.0
 */
public abstract class AbstractFeatureArray<T> implements FeatureArray<T> {

	private final FeatureModel<T> featureModel;
	private final int size;
	private final FeatureSpecification specification;

	protected AbstractFeatureArray(@NonNull FeatureModel<T> featureModel) {
		this.featureModel = featureModel;

		specification = featureModel.getSpecification();
		size = specification.size();
	}

	@Override
	public String toString() {
		return "AbstractFeatureArray[" + size + "]";
	}

	@Override
	public int compareTo(@NonNull FeatureArray<T> o) {
		sizeCheck(o);

		FeatureType<T> featureType = featureModel.getFeatureType();
		for (int i = 0; i < size(); i++) {
			T x = get(i);
			T y = o.get(i);
			int comparison = featureType.compare(x, y);
			if (comparison != 0) {
				return comparison;
			}
			// Else, do nothing; the loop will check the next value
		}
		// If we get to the end, then all values must be equal
		return 0;
	}

	@Override
	public int size() {
		return size;
	}

	@NonNull
	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

	@NonNull
	@Override
	public FeatureSpecification getSpecification() {
		return specification;
	}

	@Override
	public int hashCode() {
		return specification.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof AbstractFeatureArray)) return false;
		FeatureArray<?> that = (FeatureArray<?>) obj;
		for (int i = 0; i < size; i++) {
			T t1 = get(i);
			Object t2 = that.get(i);
			if (!Objects.equals(t1, t2)) {
				return false;
			}
		}
		return specification.equals(that.getSpecification());
	}

	protected final void sizeCheck(@NonNull FeatureArray<T> o) {
		if (size() != o.size()) throw buildException(o);
	}

	@NonNull
	private IllegalArgumentException buildException(
			@NonNull FeatureArray<T> featureArray
	) {
		String message = Templates.create()
				.add("Attempting to compare objects with different specified")
				.add("feature sizes. This: {} vs {}")
				.with(size(), featureArray.size())
				.data(this, featureArray)
				.build();
		return new IllegalArgumentException(message);
	}
}
