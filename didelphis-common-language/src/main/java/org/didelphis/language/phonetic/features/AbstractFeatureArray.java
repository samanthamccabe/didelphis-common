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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Class {@code AbstractFeatureArray}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0 Date: 2017-06-15
 */
public abstract class AbstractFeatureArray<T> implements FeatureArray<T> {
	private final FeatureSpecification specification;
	private final FeatureModel<T> featureModel;

	protected AbstractFeatureArray(@NotNull FeatureModel<T> featureModel) {
		this.featureModel = featureModel;
		this.specification = featureModel.getSpecification();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof AbstractFeatureArray)) {return false;}
		FeatureArray<?> that = (FeatureArray<?>) obj;
		for (int i = 0; i < specification.size(); i++) {
			T t1 = get(i);
			Object t2 = that.get(i);
			if (!Objects.equals(t1, t2)) {
				return false;
			}
		}
		return specification.equals(that.getSpecification());
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(@NotNull FeatureArray<T> o) {
		if (size() != o.size()) {
			throw new IllegalArgumentException(
					"Attempting to compare arrays of different lengths");
		}
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
		return specification.size();
	}

	@NotNull
	@Override
	public FeatureModel<T> getFeatureModel() {
		return featureModel;
	}

	@Override
	public FeatureSpecification getSpecification() {
		return specification;
	}

	@Override
	public int hashCode() {
		return Objects.hash(specification, featureModel);
	}
}
