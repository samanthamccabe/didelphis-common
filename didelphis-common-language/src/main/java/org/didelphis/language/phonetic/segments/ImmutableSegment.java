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

package org.didelphis.language.phonetic.segments;

import lombok.NonNull;
import org.didelphis.language.phonetic.features.EmptyFeatureArray;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.model.FeatureModel;

/**
 * Class {@code ImmutableSegment}
 *
 * @author Samantha Fiona McCabe
 * @since 0.1.0
 */
public class ImmutableSegment<T> extends StandardSegment<T> {

	/**
	 * Copy constructor -
	 *
	 * @param segment the {@link Segment} to be copied
	 */
	public ImmutableSegment(@NonNull Segment<T> segment) {
		super(segment);
	}

	/**
	 * Standard constructor -
	 *
	 * @param symbol the phonetic symbol representing this segment
	 * @param featureArray the feature array representing this segment
	 */
	public ImmutableSegment(String symbol, FeatureArray<T> featureArray) {
		super(symbol, featureArray);
	}
	
	public ImmutableSegment(String symbol, FeatureModel<T> model) {
		this(symbol, new EmptyFeatureArray<>(model));
	}

	@Override
	public boolean alter(@NonNull Segment<T> segment) {
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ImmutableSegment)) return false;
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return 31 * ImmutableSegment.class.hashCode() * super.hashCode();
	}

	@NonNull
	@Override
	public String toString() {
		return super.toString() + "(immutable)";
	}
}
