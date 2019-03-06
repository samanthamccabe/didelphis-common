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
 * @date 2017-06-15
 * @since 0.1.0
 */
public abstract class AbstractFeatureArray<T> implements FeatureArray<T> {
	private final FeatureModel<T> featureModel;

	protected AbstractFeatureArray(@NonNull FeatureModel<T> featureModel) {
		this.featureModel = featureModel;
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
		return getSpecification().size();
	}

	@NonNull
	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

	@NonNull
	@Override
	public FeatureSpecification getSpecification() {
		return featureModel.getSpecification();
	}

	@Override
	public int hashCode() {
		return featureModel.getSpecification().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof AbstractFeatureArray)) return false;
		FeatureArray<?> that = (FeatureArray<?>) obj;
		for (int i = 0; i < getSpecification().size(); i++) {
			T t1 = get(i);
			Object t2 = that.get(i);
			if (!Objects.equals(t1, t2)) {
				return false;
			}
		}
		return getSpecification().equals(that.getSpecification());
	}

	protected void sizeCheck(@NonNull FeatureArray<T> o) {
		if (size() != o.size()) throw buildException(o);
	}

	@NonNull
	private RuntimeException buildException(@NonNull FeatureArray<T> o) {
		String message = Templates.create()
				.add("Attempting to compare objects with different specified")
				.add("feature sizes. This: {} vs {}")
				.with(size(), o.size())
				.data(this, o)
				.build();
		return new IllegalArgumentException(message);
	}
}
