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

package org.didelphis.language.phonetic.segments;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.didelphis.language.phonetic.features.EmptyFeatureArray;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.model.FeatureModel;

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
 */
@EqualsAndHashCode(exclude = "model")
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
