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

import java.util.Objects;

/**
 * Class {@code UndefinedSegment}
 * Stores segments which are not identifiable within a {@link FeatureModel} or 
 * {@link org.didelphis.language.phonetic.model.FeatureMapping} and cannot be
 * defined in terms of features. It is only defined by it's symbol, but still
 * provides provenance for the model that generated it.
 * 
 * This is included for compatibility, and to avoid errors, namely that a {@link
 * StandardSegment} with no features will return {@code true} when {@link 
 * #matches(Segment)} is called on another segment, which is not the desired
 * behavior.
 * 
 * @author Samantha Fiona McCabe
 * @date 12/29/17
 */
public class UndefinedSegment<T> implements Segment<T> {

	private final FeatureModel<T> model;
	private final FeatureArray<T> features;
	private final String symbol;

	public UndefinedSegment(
			@NonNull String symbol,
			@NonNull FeatureModel<T> model) {
		this.model = model;
		this.symbol = symbol;
		
		features = new EmptyFeatureArray<>(model);
	}

	@Override
	public int hashCode() {
		return 17 * (31 + symbol.hashCode());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UndefinedSegment)) return false;
		UndefinedSegment<?> segment = (UndefinedSegment<?>) o;
		return Objects.equals(symbol, segment.symbol);
	}
	
	@Override
	public boolean alter(@NonNull Segment<T> segment) {
		// no-op
		return false;
	}
	
	@NonNull
	@Override
	public String getSymbol() {
		return symbol;
	}

	@NonNull
	@Override
	public FeatureArray<T> getFeatures() {
		return features;
	}

	@Override
	public boolean isDefinedInModel() {
		return false;
	}

	@Override
	public String toString() {
		return symbol;
	}

	@NonNull
	@Override
	public FeatureModel<T> getFeatureModel() {
		return model;
	}
}
