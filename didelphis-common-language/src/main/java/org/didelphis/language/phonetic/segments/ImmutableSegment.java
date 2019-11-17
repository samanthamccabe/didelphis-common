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

import lombok.NonNull;

import org.didelphis.language.phonetic.features.EmptyFeatureArray;
import org.didelphis.language.phonetic.features.FeatureArray;
import org.didelphis.language.phonetic.model.FeatureModel;

/**
 * Class {@code ImmutableSegment}
 *
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
